package com.example.scansaveradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    AppCompatSpinner chooseResetMethod;
    AppCompatButton adminResetPasswordBtn;
    TextInputEditText methodInput;
    AppCompatTextView phStart;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_forgot_password);

        chooseResetMethod = (AppCompatSpinner) findViewById(R.id.chooseResetMethod);
        adminResetPasswordBtn = (AppCompatButton) findViewById(R.id.adminResetPasswordBtn);
        methodInput = (TextInputEditText) findViewById(R.id.methodInput);
        phStart = (AppCompatTextView) findViewById(R.id.phStart);

        mAuth = FirebaseAuth.getInstance();

        adminResetPasswordBtn.setOnClickListener(this);
        chooseResetMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (chooseResetMethod.getSelectedItem().equals("Email")) {
                    methodInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    phStart.setVisibility(View.GONE);
                } else {
                    methodInput.setInputType(InputType.TYPE_CLASS_PHONE);
                    phStart.setVisibility(View.VISIBLE);
                }
                methodInput.clearFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminForgotPasswordActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        String inputValue = methodInput.getText().toString();
        String methodValue = chooseResetMethod.getSelectedItem().toString();

        if (methodValue.equals("Email")) {
            if (inputValue.isEmpty()) {
                methodInput.setError("Email is required!");
                methodInput.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputValue).matches()) {         //verify that email address is a valid email
                methodInput.setError("Please enter a valid email address.");
                methodInput.requestFocus();
            } else {
                mAuth.sendPasswordResetEmail(inputValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AdminForgotPasswordActivity.this, "Please check your email.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AdminForgotPasswordActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        } else {
            if (inputValue.isEmpty()) {
                methodInput.setError("Phone Number is required!");
                methodInput.requestFocus();
            } else if (inputValue.length() != 10) {
                methodInput.setError("Invalid Phone Number!");
                methodInput.requestFocus();
            } else if (!Patterns.PHONE.matcher(inputValue).matches()) {         //verify that email address is a phone number
                methodInput.setError("Please enter a valid Phone Number.");
                methodInput.requestFocus();
            } else {
                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNumber").equalTo(inputValue);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            methodInput.setError(null);

                            Intent verify = new Intent(AdminForgotPasswordActivity.this, AdminVerifyActivity.class);
                            verify.putExtra("phone", inputValue);
                            verify.putExtra("verified", "updateData");
                            startActivity(verify);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        methodInput.setError("User does not exist!");
                    }
                });
            }
        }
    }
}