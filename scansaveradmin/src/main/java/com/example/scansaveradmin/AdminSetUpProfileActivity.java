package com.example.scansaveradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.scansaveradmin.helpers.ProfilePhotoModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminSetUpProfileActivity extends AppCompatActivity {

    CircleImageView uploadProfilePhoto;
    AppCompatButton completeSetUpBtn;

    private DatabaseReference imageReference = FirebaseDatabase.getInstance().getReference("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());
    private FirebaseStorage firebaseStorage;
    private StorageReference imageStorageRef;
    private Uri imageUri;

    public static String userID;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_set_up_profile);

        uploadProfilePhoto = findViewById(R.id.uploadProfilePhoto);
        completeSetUpBtn = findViewById(R.id.completeSetUpBtn);

        //storage
        firebaseStorage = FirebaseStorage.getInstance();
        imageStorageRef = firebaseStorage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        completeSetUpBtn.setEnabled(false);

        uploadProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        completeSetUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminSetUpProfileActivity.this, AdminWelcomeActivity.class));
            }
        });
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
            uploadProfilePhoto.setImageURI(imageUri);
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
                                Toast.makeText(AdminSetUpProfileActivity.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                completeSetUpBtn.setEnabled(true);
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
                        Toast.makeText(AdminSetUpProfileActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}