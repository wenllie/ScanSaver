package com.example.scansaveradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class AdminVerifyActivity extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText verifyCode;
    AppCompatTextView userPhoneNumber, resendOTP;
    AppCompatButton verifyBtn;
    FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verify);

        verifyCode = (TextInputEditText) findViewById(R.id.verifyCode);
        verifyBtn = (AppCompatButton) findViewById(R.id.verifyCodeBtn);
        userPhoneNumber = (AppCompatTextView) findViewById(R.id.phoneNumber);
        resendOTP = (AppCompatTextView) findViewById(R.id.resendOTP);

        userPhoneNumber.setText(String.format(
                "+63-%s", getIntent().getStringExtra("phone")
        ));

        verificationId = getIntent().getStringExtra("verificationId");

        mAuth = FirebaseAuth.getInstance();

        resendOTP.setOnClickListener(this);
        verifyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String verifycode = verifyCode.getText().toString().trim();

        switch (view.getId()) {
            case R.id.resendOTP:
                Toast.makeText(AdminVerifyActivity.this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.verifyCodeBtn:
                if (verifycode.isEmpty()) {
                    Toast.makeText(AdminVerifyActivity.this, "Please enter the OTP!", Toast.LENGTH_SHORT).show();
                    verifyCode.setError("Please enter the code!");
                    verifyCode.requestFocus();
                } else {
                    if (verificationId != null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verifycode);
                        mAuth.getCurrentUser()
                                .linkWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent login = new Intent(AdminVerifyActivity.this, AdminSetUpProfileActivity.class);
                                            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(login);
                                            finish();
                                        } else {
                                            Toast.makeText(AdminVerifyActivity.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                break;
        }
    }
}