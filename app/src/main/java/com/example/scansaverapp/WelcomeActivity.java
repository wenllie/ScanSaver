package com.example.scansaverapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class WelcomeActivity extends AppCompatActivity {

    AppCompatButton welcomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeBtn = (AppCompatButton) findViewById(R.id.welcomeBtn);
        LottieAnimationView animated_welcome = findViewById(R.id.animated_welcome);


        welcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent welcome = new Intent(WelcomeActivity.this, UserNavDrawer.class);
                startActivity(welcome);
                finish();
            }
        });
    }
}