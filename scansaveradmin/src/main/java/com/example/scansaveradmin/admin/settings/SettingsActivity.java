package com.example.scansaveradmin.admin.settings;

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

import com.example.scansaveradmin.AdminNavDrawerActivity;
import com.example.scansaveradmin.R;
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
    SwitchCompat darkModeSwitchBtn;
    AppCompatButton editProfileBtn;
    RelativeLayout privacy_security_layout, aboutUsLayout;

    private static final String NIGHT_MODE_PREF = "PREF_NIGHT_MODE";

    private DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());
    private FirebaseStorage firebaseStorage;
    private StorageReference imageStorageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        frSettingsToDashboard = findViewById(R.id.frSettingsToDashboard);
        registeredFullName = findViewById(R.id.registeredFullName);
        registeredEmail = findViewById(R.id.registeredEmail);
        darkModeSwitchBtn = findViewById(R.id.darkModeSwitchBtn);
        privacy_security_layout = findViewById(R.id.privacy_security_layout);
        aboutUsLayout = findViewById(R.id.aboutUsLayout);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        profileImage = findViewById(R.id.profileImage);

        //storage
        firebaseStorage = FirebaseStorage.getInstance();
        imageStorageRef = firebaseStorage.getReference();

        //fetch admin details in the database
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                registeredFullName.setText(snapshot.child("Personal Details").child("fullName").getValue().toString());
                registeredEmail.setText(snapshot.child("Personal Details").child("email").getValue().toString());

                Picasso.get().load(snapshot.child("Profile Photo").child("profileUrl").getValue().toString()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Dark Mode Toggle Settings
        //the switch will be automatically turned on
        /*SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        darkModeSwitchBtn.setChecked(sharedPreferences.getBoolean("value", true));*/
        darkModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (darkModeSwitchBtn.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences(NIGHT_MODE_PREF, MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    darkModeSwitchBtn.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(NIGHT_MODE_PREF, MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    darkModeSwitchBtn.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        frSettingsToDashboard.setOnClickListener(this);
        privacy_security_layout.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        aboutUsLayout.setOnClickListener(this);
        profileImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frSettingsToDashboard:
                startActivity(new Intent(SettingsActivity.this, AdminNavDrawerActivity.class));
                finish();
                break;
            case R.id.privacy_security_layout:
                startActivity(new Intent(SettingsActivity.this, PrivacySecurityActivity.class));
                break;
            case R.id.aboutUsLayout:
                aboutUsShowDialog();
                break;
            case R.id.editProfileBtn:
                startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class));
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
        StorageReference storageRef = imageStorageRef.child("admin/" + FirebaseAuth.getInstance().getUid() + "/"
                + imageKey);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ProfilePhotoModel profilePhotoModel = new ProfilePhotoModel(uri.toString());
                                imageReference.child("Profile Photo").setValue(profilePhotoModel);
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

    @SuppressLint("ResourceAsColor")
    private void aboutUsShowDialog() {

        Dialog aboutUsDialog = new Dialog(this);
        aboutUsDialog.setContentView(R.layout.dialog_box_about_us);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(aboutUsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        aboutUsDialog.show();
        aboutUsDialog.getWindow().setAttributes(lp);
        aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    }
}