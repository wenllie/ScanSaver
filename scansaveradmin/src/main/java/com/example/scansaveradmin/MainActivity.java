package com.example.scansaveradmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

    TextInputEditText adminEmail, adminPassword;
    AppCompatButton adminSignInBtn, addAccount;
    MaterialTextView adminForgotPassword;

    FirebaseAuth mAuth;

    //shared preferences
    private static final String PREF_LOGIN = "LOGIN_PREF";
    private static final String USER_EMAIL = "EMAIL_ADDRESS";
    private static final String USER_PASSWORD = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addAccount = (AppCompatButton) findViewById(R.id.addAccount);
        adminSignInBtn = (AppCompatButton) findViewById(R.id.adminSignInBtn);
        adminEmail = (TextInputEditText) findViewById(R.id.adminEmail);
        adminPassword = (TextInputEditText) findViewById(R.id.adminPassword);
        adminForgotPassword = (MaterialTextView) findViewById(R.id.adminForgotPassword);

        mAuth = FirebaseAuth.getInstance();

        addAccount.setOnClickListener(this);
        adminSignInBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addAccount:
                startActivity(new Intent(MainActivity.this, AdminRegisterActivity.class));
                finish();
                break;
            case R.id.adminSignInBtn:
                signInAdmin();
                break;
        }
    }

    private void signInAdmin() {
        String email = adminEmail.getText().toString();
        String password = adminPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            adminEmail.setError("Email is required!");
            adminEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            adminPassword.setError("Password is required!");
            adminPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (mAuth.getCurrentUser().isEmailVerified()){

                            SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE).edit();
                            editor.putString(USER_EMAIL, email);
                            editor.putString(USER_PASSWORD, password);
                            editor.commit();

                            startActivity(new Intent(MainActivity.this, AdminNavDrawerActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Please verify your email address!", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                            FirebaseAuth.getInstance().signOut();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Log in error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}