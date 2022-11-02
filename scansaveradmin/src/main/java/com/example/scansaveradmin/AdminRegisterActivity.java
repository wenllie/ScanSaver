package com.example.scansaveradmin;

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

import com.example.scansaveradmin.helpers.AdminDetails;
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

public class AdminRegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //initialize variables
    TextInputEditText adminFullName, adminBirthday, adminAge, adminPhoneNumber, adminEmailRegister, adminPasswordRegister, adminConfirmPassword;
    Spinner adminGender;
    AppCompatButton adminAddAccountBtn, dateBtn;
    TextView adminSignIn;
    CheckBox termsConditionsCheckbox;
    private MaterialAlertDialogBuilder termsDialog;
    private int mYear, mMonth, mDay;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        //declare variables
        adminFullName = (TextInputEditText) findViewById(R.id.adminFullName);
        adminBirthday = (TextInputEditText) findViewById(R.id.adminBirthday);
        adminAge = (TextInputEditText) findViewById(R.id.adminAge);
        adminGender = (Spinner) findViewById(R.id.adminGender);
        adminPhoneNumber = (TextInputEditText) findViewById(R.id.adminPhoneNumber);
        adminEmailRegister = (TextInputEditText) findViewById(R.id.adminEmailRegister);
        adminPasswordRegister = (TextInputEditText) findViewById(R.id.adminPasswordRegister);
        adminConfirmPassword = (TextInputEditText) findViewById(R.id.adminConfirmPassword);
        adminSignIn = (TextView) findViewById(R.id.signInBtn);
        adminAddAccountBtn = (AppCompatButton) findViewById(R.id.adminAddAccountBtn);
        dateBtn = (AppCompatButton) findViewById(R.id.dateBtn);
        termsConditionsCheckbox = (CheckBox) findViewById(R.id.termsConditionsCheckbox);

        mAuth = FirebaseAuth.getInstance();

        adminAddAccountBtn.setEnabled(false);
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
                            adminAddAccountBtn.setEnabled(true);
                            dialogInterface.dismiss();
                        }
                    });
                    termsConditionsCheckbox.setChecked(true);
                    termsDialog.show();
                }
            }
        });

        //Function for buttons
        adminSignIn.setOnClickListener(this);
        adminAddAccountBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminRegisterActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInBtn:
                startActivity(new Intent(AdminRegisterActivity.this, MainActivity.class));
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
                                adminBirthday.setText(day + "-" + (month + 1) + "-" + year);
                                birthday.set(year, month, day);
                                c.set(mYear, mMonth, mDay);
                                int age = c.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
                                if (c.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
                                    age--;
                                }
                                Integer ageInt = new Integer(age);
                                adminAge.setText(ageInt.toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.adminAddAccountBtn:
                createAdminProfile();
                break;
        }
    }

    private void createAdminProfile() {

        //extract data from edit text
        String fullName = adminFullName.getText().toString();
        String gender = adminGender.getSelectedItem().toString();
        String birthday = adminBirthday.getText().toString();
        String age = adminAge.getText().toString();
        String phoneNumber = adminPhoneNumber.getText().toString();
        String email = adminEmailRegister.getText().toString();
        String password = adminPasswordRegister.getText().toString();
        String confirmPassword = adminConfirmPassword.getText().toString();

        //verify that edit text fields are not empty
        if (fullName.isEmpty()) {
            adminFullName.setError("Full Name is required!");
            adminFullName.requestFocus();
            return;
        }
        if (gender.isEmpty()) {
            adminGender.requestFocus();
            return;
        }
        if (birthday.isEmpty()) {
            adminBirthday.setError("Birthday is required!");
            adminBirthday.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            adminAge.setError("Age is required!");
            adminAge.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            adminPhoneNumber.setError("Phone Number is required!");
            adminPhoneNumber.requestFocus();
            return;
        }
        if (phoneNumber.length() != 10) {
            adminPhoneNumber.setError("Invalid Phone Number!");
            adminPhoneNumber.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {         //verify that email address is a valid email
            adminPhoneNumber.setError("Please enter a valid Phone Number.");
            adminPhoneNumber.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            adminEmailRegister.setError("Email is required!");
            adminEmailRegister.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {         //verify that email address is a valid email
            adminEmailRegister.setError("Please enter a valid email address.");
            adminEmailRegister.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            adminPasswordRegister.setError("Password is required!");
            adminPasswordRegister.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            adminConfirmPassword.setError("Confirm password is required!");
            adminConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {         //verify that password and confirm password is the same
            adminPasswordRegister.setError("Password do not match!");
            adminPasswordRegister.requestFocus();
            adminConfirmPassword.setError("Password do not match!");
            adminConfirmPassword.requestFocus();
            return;
        }
        if (password.equals(confirmPassword)) {

            //Create admin profile
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser adminUser = mAuth.getCurrentUser();


                            //store user data into realtime database
                            AdminDetails userDetails = new AdminDetails(fullName, gender, birthday, age, phoneNumber, email);

                            //extracting user reference from database for "Registered Employers"
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");

                            reference.child("Admin Users").child(adminUser.getUid()).child("Personal Details").setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        //send email verification
                                        adminUser.sendEmailVerification();

                                        // to get the user ID for verifying user
//                                    UserVerifyActivity.userID = user.getUid();

                                        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                            @Override
                                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                            }

                                            @Override
                                            public void onVerificationFailed(FirebaseException e) {
                                                Toast.makeText(AdminRegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull String verificationId,
                                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                                //verify user after successful registration
                                                Intent verify = new Intent(AdminRegisterActivity.this, AdminVerifyActivity.class);
                                                //prevent user from returning back to the register page on pressing back button after registration
                                                verify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                verify.putExtra("phone", adminPhoneNumber.getText().toString().trim());
                                                verify.putExtra("verificationId", verificationId);
                                                startActivity(verify);
                                                finish();
                                            }
                                        };
                                        PhoneAuthOptions options =
                                                PhoneAuthOptions.newBuilder(mAuth)
                                                        .setPhoneNumber("+63" + adminPhoneNumber.getText().toString().trim())       // Phone number to verify
                                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                        .setActivity(AdminRegisterActivity.this)                 // Activity (for callback binding)
                                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                        .build();
                                        PhoneAuthProvider.verifyPhoneNumber(options);

                                    } else {
                                        Toast.makeText(AdminRegisterActivity.this, "Registration failed! Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            adminPasswordRegister.setError("Password is too weak! Please use combination of alphabets, numbers and special characters.");
                            adminPasswordRegister.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            adminEmailRegister.setError("Email is invalid or already in use! Please use another email.");
                            adminEmailRegister.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            adminEmailRegister.setError("User already registered with this email! Please use another email.");
                            adminEmailRegister.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(AdminRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(AdminRegisterActivity.this, "Too much admin!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}