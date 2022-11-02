package com.example.scansaverapp.users.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.ProfilePhotoModel;
import com.example.scansaverapp.users.expenses.ExpensesAnalyticsActivity;
import com.example.scansaverapp.users.helpcenter.HelpCenterActivity;
import com.example.scansaverapp.users.spending.SpendingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frSettingsToDashboard;
    CircleImageView profileImage;
    AppCompatTextView registeredFullName, registeredEmail;
    RelativeLayout privacySecurityBtn, frSettingsToHelpCenter, aboutUsBtn, themeLayout, editProfileBtn;

    private DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference("Customers").child("Profile Photo").child(FirebaseAuth.getInstance().getUid());
    private FirebaseStorage firebaseStorage;
    private StorageReference imageStorageRef;
    private Uri imageUri;

    //theme
    ThemePref themePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themePref = new ThemePref(this);
        if (themePref.loadNightModeState() == 2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (themePref.loadNightModeState() == 1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        frSettingsToDashboard = (ImageView) findViewById(R.id.frSettingsToDashboard);
        registeredFullName = (AppCompatTextView) findViewById(R.id.registeredFullName);
        registeredEmail = (AppCompatTextView) findViewById(R.id.registeredEmail);
        editProfileBtn = (RelativeLayout) findViewById(R.id.editProfileBtn);
        privacySecurityBtn = (RelativeLayout) findViewById(R.id.privacySecurityBtn);
        frSettingsToHelpCenter = (RelativeLayout) findViewById(R.id.frSettingsToHelpCenter);
        aboutUsBtn = (RelativeLayout) findViewById(R.id.aboutUsBtn);
        themeLayout = (RelativeLayout) findViewById(R.id.themeLayout);
        profileImage = (CircleImageView) findViewById(R.id.profileImage);

        //storage
        firebaseStorage = FirebaseStorage.getInstance();
        imageStorageRef = firebaseStorage.getReference();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Personal Information").child(FirebaseAuth.getInstance().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                registeredFullName.setText(snapshot.child("fullName").getValue().toString());
                registeredEmail.setText(snapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference profilePhotoReference = FirebaseDatabase.getInstance().getReference("Customers").child("Profile Photo").child(FirebaseAuth.getInstance().getUid());

        profilePhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Picasso.get().load(snapshot.child("profileUrl").getValue().toString()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //app theme light, dark, system
        themeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus themeDialog = DialogPlus.newDialog(SettingsActivity.this)
                        .setContentHolder(new ViewHolder(R.layout.pop_up_set_theme))
                        .setExpanded(true, 1000)
                        .create();

                View v = themeDialog.getHolderView();

                RadioGroup radioGroup = v.findViewById(R.id.radioGroupTheme);
                RadioButton radioLight = v.findViewById(R.id.radioLight);
                RadioButton radioDark = v.findViewById(R.id.radioDark);
                RadioButton radioSystem = v.findViewById(R.id.radioSystem);

                if (themePref.loadNightModeState() == 2){
                    radioDark.setChecked(true);
                } else if (themePref.loadNightModeState() == 1){
                    radioLight.setChecked(true);
                } else {
                    radioSystem.setChecked(true);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {

                        int id = radioGroup.getCheckedRadioButtonId();

                        switch (id) {
                            case R.id.radioLight:
                                themePref.setNightModeState(1);
                                restartApp();
                                break;

                            case R.id.radioDark:
                                themePref.setNightModeState(2);
                                restartApp();
                                break;

                            case R.id.radioSystem:
                                themePref.setNightModeState(-1);
                                restartApp();
                                break;
                        }
                    }
                });

                themeDialog.show();
            }
        });

        //set on click for buttons
        editProfileBtn.setOnClickListener(this);
        frSettingsToDashboard.setOnClickListener(this);
        privacySecurityBtn.setOnClickListener(this);
        frSettingsToHelpCenter.setOnClickListener(this);
        aboutUsBtn.setOnClickListener(this);
        profileImage.setOnClickListener(this);
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(SettingsActivity.this, UserNavDrawer.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editProfileBtn:
                Intent editProfile = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
                finish();
                break;

            case R.id.frSettingsToDashboard:
                onBackPressed();
                break;

            case R.id.privacySecurityBtn:
                startActivity(new Intent(SettingsActivity.this, PrivacySecurityActivity.class));
                finish();
                break;

            case R.id.frSettingsToHelpCenter:
                startActivity(new Intent(SettingsActivity.this, HelpCenterActivity.class));
                finish();
                break;

            case R.id.aboutUsBtn:
                final DialogPlus aboutUsDialog = DialogPlus.newDialog(SettingsActivity.this)
                        .setContentHolder(new ViewHolder(R.layout.dialog_box_about_us))
                        .setExpanded(true, 1500)
                        .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                        .create();

                aboutUsDialog.show();
                break;

            case R.id.profileImage:
                choosePhoto();
                break;
        }
    }

    private void choosePhoto() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            uploadPhoto();
        }
    }

    private void uploadPhoto() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image...");
        progressDialog.show();

        final String imageKey = UUID.randomUUID().toString();
        StorageReference storageRef = imageStorageRef.child("customers/" + FirebaseAuth.getInstance().getUid() + "/"
                + imageKey);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ProfilePhotoModel profilePhotoModel = new ProfilePhotoModel(uri.toString());
                                imageReference.setValue(profilePhotoModel);
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress " + (int) progressPercent + "%");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}