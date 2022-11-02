package com.example.scansaverapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scansaverapp.helpers_database.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //initialize variables
    TextInputEditText userFullName, userBirthday, userAge, userPhoneNumber, userEmailRegister, userPasswordRegister, userConfirmPassword;
    Spinner userGender;
    AppCompatButton userRegisterBtn, dateBtn;
    TextView userSignIn;
    CheckBox termsConditionsCheckbox;
    private MaterialAlertDialogBuilder termsDialog;
    private int mYear, mMonth, mDay;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //declare variables
        userFullName = (TextInputEditText) findViewById(R.id.userFullName);
        userBirthday = (TextInputEditText) findViewById(R.id.userBirthday);
        userAge = (TextInputEditText) findViewById(R.id.userAge);
        userGender = (Spinner) findViewById(R.id.userGender);
        userPhoneNumber = (TextInputEditText) findViewById(R.id.userPhoneNumber);
        userEmailRegister = (TextInputEditText) findViewById(R.id.userEmailRegister);
        userPasswordRegister = (TextInputEditText) findViewById(R.id.userPasswordRegister);
        userConfirmPassword = (TextInputEditText) findViewById(R.id.userConfirmPassword);
        userSignIn = (TextView) findViewById(R.id.signInBtn);
        userRegisterBtn = (AppCompatButton) findViewById(R.id.userRegisterBtn);
        dateBtn = (AppCompatButton) findViewById(R.id.dateBtn);
        termsConditionsCheckbox = (CheckBox) findViewById(R.id.termsConditionsCheckbox);

        mAuth = FirebaseAuth.getInstance();

        userRegisterBtn.setEnabled(false);
        termsDialog = new MaterialAlertDialogBuilder(this, R.style.CutShapeTheme);

        termsConditionsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    termsDialog.setTitle("Terms & Conditions");
                    termsDialog.setMessage(getResources().getString(R.string.terms_conditions));
                    termsDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userRegisterBtn.setEnabled(true);
                            dialogInterface.dismiss();
                        }
                    });
                    termsConditionsCheckbox.setChecked(true);
                    termsDialog.show();
                }
            }
        });

        //Function for buttons
        userSignIn.setOnClickListener(this);
        userRegisterBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
        termsConditionsCheckbox.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInBtn:
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.dateBtn:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                final Calendar birthday = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int day) {
                                userBirthday.setText(day + "-" + (month + 1) + "-" + year);
                                birthday.set(year, month, day);
                                c.set(mYear, mMonth, mDay);
                                int age = c.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
                                if (c.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
                                    age--;
                                }
                                Integer ageInt = new Integer(age);
                                userAge.setText(ageInt.toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.userRegisterBtn:
                createUserProfile();
                break;
            case R.id.termsConditionsCheckbox:

                break;
        }
    }

    private void createUserProfile() {

        //extract data from edit text
        String fullName = userFullName.getText().toString();
        String gender = userGender.getSelectedItem().toString();
        String birthday = userBirthday.getText().toString();
        String age = userAge.getText().toString();
        String phoneNumber = userPhoneNumber.getText().toString();
        String email = userEmailRegister.getText().toString();
        String password = userPasswordRegister.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();

        //verify that edit text fields are not empty
        if (fullName.isEmpty()) {
            userFullName.setError("Full Name is required!");
            userFullName.requestFocus();
            return;
        } else {
            userFullName.clearFocus();
        }
        if (gender.isEmpty()) {
            userGender.requestFocus();
            return;
        }
        if (birthday.isEmpty()) {
            userBirthday.setError("Birthday is required!");
            userBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            userAge.setError("Age is required!");
            userAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            userPhoneNumber.setError("Phone Number is required!");
            userPhoneNumber.requestFocus();
            return;
        }
        if (phoneNumber.length() != 10) {
            userPhoneNumber.setError("Invalid Phone Number!");
            userPhoneNumber.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {         //verify that email address is a valid email
            userPhoneNumber.setError("Please enter a valid Phone Number.");
            userPhoneNumber.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            userEmailRegister.setError("Email is required!");
            userEmailRegister.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {         //verify that email address is a valid email
            userEmailRegister.setError("Please enter a valid email address.");
            userEmailRegister.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            userPasswordRegister.setError("Password is required!");
            userPasswordRegister.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            userConfirmPassword.setError("Confirm password is required!");
            userConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {         //verify that password and confirm password is the same
            userPasswordRegister.setError("Password do not match!");
            userPasswordRegister.requestFocus();
            userConfirmPassword.setError("Password do not match!");
            userConfirmPassword.requestFocus();
            return;
        }
        if (password.equals(confirmPassword)) {

            //Create user profile
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        //store user data into realtime database
                        UserDetails userDetails = new UserDetails(fullName, gender, birthday, age, phoneNumber, email, user.getUid());

                        //extracting user reference from database for "Registered Employers"
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers").child("Personal Information");

                        reference.child(user.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    //send email verification
                                    user.sendEmailVerification();

                                    // to get the user ID for verifying user
//                                    UserVerifyActivity.userID = user.getUid();

                                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                        @Override
                                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                                        }

                                        @Override
                                        public void onVerificationFailed(FirebaseException e) {
                                            Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String verificationId,
                                                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                            //verify user after successful registration
                                            Intent verify = new Intent(RegisterActivity.this, UserVerifyActivity.class);
                                            //prevent user from returning back to the register page on pressing back button after registration
                                            verify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            verify.putExtra("phone", userPhoneNumber.getText().toString().trim());
                                            verify.putExtra("verificationId", verificationId);
                                            startActivity(verify);
                                            finish();
                                        }
                                    };
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(mAuth)
                                                    .setPhoneNumber("+63" + userPhoneNumber.getText().toString().trim())       // Phone number to verify
                                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(RegisterActivity.this)                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration failed! Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            userPasswordRegister.setError("Password is too weak! Please use combination of alphabets, numbers and special characters.");
                            userPasswordRegister.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            userEmailRegister.setError("Email is invalid or already in use! Please use another email.");
                            userEmailRegister.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            userEmailRegister.setError("User already registered with this email! Please use another email.");
                            userEmailRegister.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}