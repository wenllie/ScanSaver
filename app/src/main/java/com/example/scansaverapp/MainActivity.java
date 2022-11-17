package com.example.scansaverapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextInputEditText userEmail, userPassword;
    AppCompatButton userSignInBtn, userRegisterBtn;
    MaterialTextView userForgotPassword;

    public static String userID;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmail = (TextInputEditText) findViewById(R.id.userEmail);
        userPassword = (TextInputEditText) findViewById(R.id.userPassword);
        userSignInBtn = (AppCompatButton) findViewById(R.id.userSignInBtn);
        userRegisterBtn = (AppCompatButton) findViewById(R.id.userRegister);
        userForgotPassword = (MaterialTextView) findViewById(R.id.userForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        userSignInBtn.setOnClickListener(this);
        userRegisterBtn.setOnClickListener(this);
        userForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.userSignInBtn:
                loginUser();
                break;
            case R.id.userRegister:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.userForgotPassword:
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
                finish();
                break;
        }
    }

    private void loginUser() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            userEmail.setError("Email is required!");
            userEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            userPassword.setError("Password is required!");
            userPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (mAuth.getCurrentUser().isEmailVerified()){

                            startActivity(new Intent(MainActivity.this, UserNavDrawer.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Please verify your email address!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                            FirebaseAuth.getInstance().signOut();
                            clearField();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Log in error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void clearField() {

        userEmail.setText("");
        userEmail.clearFocus();
        userPassword.setText("");
        userPassword.clearFocus();
    }
}