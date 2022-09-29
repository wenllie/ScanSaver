package com.example.scansaverapp.users.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.ProfilePhotoModel;
import com.example.scansaverapp.users.helpcenter.HelpCenterActivity;
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
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frSettingsToDashboard;
    CircleImageView profileImage;
    AppCompatTextView registeredFullName, registeredEmail;
    AppCompatButton editProfileBtn;
    SwitchCompat darkModeToggleSwitch;
    RelativeLayout privacySecurityBtn, frSettingsToHelpCenter, expensesAnalyticsBtn, aboutUsBtn;

    private static final String NIGHT_MODE_PREF = "PREF_NIGHT_MODE";

    private DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference("Customers").child("Profile Photo").child(FirebaseAuth.getInstance().getUid());
    private FirebaseStorage firebaseStorage;
    private StorageReference imageStorageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        frSettingsToDashboard = (ImageView) findViewById(R.id.frSettingsToDashboard);
        registeredFullName = (AppCompatTextView) findViewById(R.id.registeredFullName);
        registeredEmail = (AppCompatTextView) findViewById(R.id.registeredEmail);
        editProfileBtn = (AppCompatButton) findViewById(R.id.editProfileBtn);
        privacySecurityBtn = (RelativeLayout) findViewById(R.id.privacySecurityBtn);
        frSettingsToHelpCenter = (RelativeLayout) findViewById(R.id.frSettingsToHelpCenter);
        expensesAnalyticsBtn = (RelativeLayout) findViewById(R.id.expensesAnalyticsBtn);
        aboutUsBtn = (RelativeLayout) findViewById(R.id.aboutUsBtn);
        darkModeToggleSwitch = (SwitchCompat) findViewById(R.id.darkModeToggleSwitch);
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

        //DarkMode Toggle
        /*SharedPreferences sharedPreferences = getSharedPreferences("NIGHT_MODE_PREF", MODE_PRIVATE);
        darkModeToggleSwitch.setChecked(sharedPreferences.getBoolean("value", true));*/
        darkModeToggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (darkModeToggleSwitch.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences(NIGHT_MODE_PREF, MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.commit();
                    darkModeToggleSwitch.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(NIGHT_MODE_PREF, MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.commit();
                    darkModeToggleSwitch.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        //setonclick for buttons
        editProfileBtn.setOnClickListener(this);
        frSettingsToDashboard.setOnClickListener(this);
        privacySecurityBtn.setOnClickListener(this);
        frSettingsToHelpCenter.setOnClickListener(this);
        expensesAnalyticsBtn.setOnClickListener(this);
        aboutUsBtn.setOnClickListener(this);
        profileImage.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editProfileBtn:
                Intent editProfile = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
                break;

            case R.id.frSettingsToDashboard:
                startActivity(new Intent(SettingsActivity.this, UserNavDrawer.class));
                break;

            case R.id.privacySecurityBtn:
                startActivity(new Intent(SettingsActivity.this, PrivacySecurityActivity.class));
                break;

            case R.id.frSettingsToHelpCenter:
                startActivity(new Intent(SettingsActivity.this, HelpCenterActivity.class));
                break;

            case R.id.expensesAnalyticsBtn:
                startActivity(new Intent(SettingsActivity.this, ExpensesAnalyticsActivity.class));
                break;

            case R.id.aboutUsBtn:
                Dialog termsDialog = new Dialog(this);
                termsDialog.setContentView(R.layout.dialog_box_about_us);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(termsDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                termsDialog.show();
                termsDialog.getWindow().setAttributes(lp);
                termsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
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