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

    AppCompatTextView phStart;
    TextInputEditText chooseMethod;
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

        phStart = (AppCompatTextView) findViewById(R.id.phStart);
        chooseMethod = (TextInputEditText) findViewById(R.id.chooseMethod);
        chooseResetPassword = (Spinner) findViewById(R.id.chooseResetPassword);
        resetPasswordBtn = (AppCompatButton) findViewById(R.id.resetPasswordBtn);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordBtn.setOnClickListener(this);
        chooseResetPassword.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (chooseResetPassword.getSelectedItem().equals("Email")) {
                    chooseMethod.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    phStart.setVisibility(View.GONE);
                } else {
                    chooseMethod.setInputType(InputType.TYPE_CLASS_PHONE);
                    phStart.setVisibility(View.VISIBLE);
                }
                chooseMethod.clearFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        String choose = chooseMethod.getText().toString();
        String chooseValue = chooseResetPassword.getSelectedItem().toString();

        if (chooseValue.equals("Email")) {
            if (choose.isEmpty()) {
                chooseMethod.setError("Email is required!");
                chooseMethod.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(choose).matches()) {         //verify that email address is a valid email
                chooseMethod.setError("Please enter a valid email address.");
                chooseMethod.requestFocus();
            } else {
                mAuth.sendPasswordResetEmail(choose).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        } else {
            if (choose.isEmpty()) {
                chooseMethod.setError("Phone Number is required!");
                chooseMethod.requestFocus();
            } else if (choose.length() != 10) {
                chooseMethod.setError("Invalid Phone Number!");
                chooseMethod.requestFocus();
            } else if (!Patterns.PHONE.matcher(choose).matches()) {         //verify that email address is a phone number
                chooseMethod.setError("Please enter a valid Phone Number.");
                chooseMethod.requestFocus();
            } else {
                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNumber").equalTo(choose);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            chooseMethod.setError(null);

                            Intent verify = new Intent(ForgotPasswordActivity.this, UserVerifyActivity.class);
                            verify.putExtra("phone", choose);
                            verify.putExtra("verified", "updateData");
                            startActivity(verify);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        chooseMethod.setError("User does not exist!");
                    }
                });
            }
        }
    }
}