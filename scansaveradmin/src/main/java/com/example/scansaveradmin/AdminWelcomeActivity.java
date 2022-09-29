package com.example.scansaveradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminWelcomeActivity extends AppCompatActivity {

    AppCompatButton welcomeBtn;
    AppCompatTextView fullNameWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_welcome);

        welcomeBtn = (AppCompatButton) findViewById(R.id.welcomeBtn);
        fullNameWelcome = findViewById(R.id.fullNameWelcome);
        LottieAnimationView animated_welcome = findViewById(R.id.animated_welcome);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fullNameWelcome.setText(snapshot.child("Personal Details").child("fullName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminWelcomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        welcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent welcome = new Intent(AdminWelcomeActivity.this, AdminNavDrawerActivity.class);
                startActivity(welcome);
                finish();
            }
        });
    }
}