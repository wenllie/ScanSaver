package com.example.scansaverapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView powered = findViewById(R.id.powered);
        ImageView logo = findViewById(R.id.logo_small);
        LottieAnimationView animated = findViewById(R.id.animated);

        powered.animate().translationX(1000).setDuration(1000).setStartDelay(2500);
        logo.animate().translationX(1000).setDuration(1000).setStartDelay(2500);
        animated.animate().translationX(-1000).setDuration(1000).setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 4000);

    }
}