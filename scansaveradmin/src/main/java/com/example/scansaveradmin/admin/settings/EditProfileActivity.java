package com.example.scansaveradmin.admin.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaveradmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frEditProfileToSettings;
    AppCompatButton updateProfileBtn, changePasswordBtn;
    TextInputEditText editEmail, editFullName, editGender, editBirthday, editAge, editPhoneNumber;

    private DatabaseReference adminUserReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        frEditProfileToSettings = findViewById(R.id.frEditProfileToSettings);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);
        changePasswordBtn = (AppCompatButton) findViewById(R.id.changePasswordBtn);
        editFullName = (TextInputEditText) findViewById(R.id.editFullName);
        editGender = (TextInputEditText) findViewById(R.id.editGender);
        editBirthday = (TextInputEditText) findViewById(R.id.editBirthday);
        editAge = (TextInputEditText) findViewById(R.id.editAge);
        editPhoneNumber = (TextInputEditText) findViewById(R.id.editPhoneNumber);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //set the value of text fields
        adminUserReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        adminUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                editEmail.setText( snapshot.child("Personal Details").child("email").getValue().toString() );
                editFullName.setText( snapshot.child("Personal Details").child("fullName").getValue().toString() );
                editGender.setText( snapshot.child("Personal Details").child("gender").getValue().toString() );
                editBirthday.setText( snapshot.child("Personal Details").child("birthday").getValue().toString() );
                editAge.setText( snapshot.child("Personal Details").child("age").getValue().toString() );
                editPhoneNumber.setText( snapshot.child("Personal Details").child("phoneNumber").getValue().toString() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //function for on click buttons
        frEditProfileToSettings.setOnClickListener(this);
        updateProfileBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfileActivity.this, SettingsActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {

        String fullName = editFullName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();

        switch (view.getId()) {
            case R.id.frEditProfileToSettings:
                onBackPressed();
                break;
            case R.id.updateProfileBtn:
                updateProfile(fullName, phoneNumber);
                break;
        }
    }

    private void updateProfile(String fullName, String phoneNumber) {

        HashMap adminUser = new HashMap();
        adminUser.put("fullName", fullName);
        adminUser.put("phoneNumber", phoneNumber);

        adminUserReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        adminUserReference.child("Personal Details").updateChildren(adminUser).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()){

                    editFullName.setText(fullName);
                    editPhoneNumber.setText(phoneNumber);
                    editFullName.clearFocus();

                    Toast.makeText(EditProfileActivity.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}