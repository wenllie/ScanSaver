package com.example.scansaverapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText resetPasswordUserEmail;
    Spinner chooseResetPassword;
    AppCompatButton resetPasswordBtn;
    private String verificationId;
    public static String userID;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    DatabaseReference userReference;
    FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordUserEmail = (TextInputEditText) findViewById(R.id.resetPasswordUserEmail);
        resetPasswordBtn = (AppCompatButton) findViewById(R.id.resetPasswordBtn);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        String email = resetPasswordUserEmail.getText().toString();

        if (email.isEmpty()) {
            resetPasswordUserEmail.setError("Email is required!");
            resetPasswordUserEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {         //verify that email address is a valid email
            resetPasswordUserEmail.setError("Please enter a valid email address.");
            resetPasswordUserEmail.requestFocus();
        } else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please check your email.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }
}