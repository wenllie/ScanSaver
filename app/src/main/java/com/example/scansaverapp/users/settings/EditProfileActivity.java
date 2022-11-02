package com.example.scansaverapp.users.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
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
    TextInputEditText editEmail, editFullName, editGender, editBirthday, editAge, editPhoneNumber;
    AppCompatButton updateProfileBtn, changePasswordBtn;

    private DatabaseReference userReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        frEditProfileToSettings = (ImageView) findViewById(R.id.frEditProfileToSettings);
        editEmail = (TextInputEditText) findViewById(R.id.editEmail);
        changePasswordBtn = (AppCompatButton) findViewById(R.id.changePasswordBtn);
        editFullName = (TextInputEditText) findViewById(R.id.editFullName);
        editGender = (TextInputEditText) findViewById(R.id.editGender);
        editBirthday = (TextInputEditText) findViewById(R.id.editBirthday);
        editAge = (TextInputEditText) findViewById(R.id.editAge);
        editPhoneNumber = (TextInputEditText) findViewById(R.id.editPhoneNumber);
        updateProfileBtn = (AppCompatButton) findViewById(R.id.updateProfileBtn);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        userReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Personal Information").child(FirebaseAuth.getInstance().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                editEmail.setText( snapshot.child("email").getValue().toString() );
                editFullName.setText( snapshot.child("fullName").getValue().toString() );
                editGender.setText( snapshot.child("gender").getValue().toString() );
                editBirthday.setText( snapshot.child("birthday").getValue().toString() );
                editAge.setText( snapshot.child("age").getValue().toString() );
                editPhoneNumber.setText( snapshot.child("phoneNumber").getValue().toString() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //on click
        updateProfileBtn.setOnClickListener(this);
        changePasswordBtn.setOnClickListener(this);
        frEditProfileToSettings.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toSettings = new Intent(EditProfileActivity.this, SettingsActivity.class);
        startActivity(toSettings);
        finish();
    }

    @Override
    public void onClick(View view) {
        String fullName = editFullName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();

        switch (view.getId()){
            case R.id.updateProfileBtn:
                updateProfile(fullName, phoneNumber);
                break;
            case R.id.changePasswordBtn:
                startActivity(new Intent(EditProfileActivity.this, UpdatePasswordActivity.class));
                finish();
                break;
            case R.id.frEditProfileToSettings:
                onBackPressed();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void updateProfile(String fullName, String phoneNumber) {

        HashMap user = new HashMap();
        user.put("fullName", fullName);
        user.put("phoneNumber", phoneNumber);

        userReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Personal Information").child(FirebaseAuth.getInstance().getUid());

        userReference.updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
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