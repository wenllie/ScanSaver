package com.example.scansaverapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private static final String PREF_LOGIN = "LOGIN_PREF";
    private static final String USER_EMAIL = "EMAIL_ADDRESS";
    private static final String USER_PASSWORD = "PASSWORD";
    private static final String NIGHT_MODE_PREF = "PREF_NIGHT_MODE";

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

                SharedPreferences nightMode = getSharedPreferences(NIGHT_MODE_PREF, MODE_PRIVATE);

                SharedPreferences preferences = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE);
                Intent intent = null;
                if(preferences.contains(USER_EMAIL)&&preferences.contains(USER_PASSWORD)){
                    if (nightMode.getBoolean("value", true)) {

                        intent = new Intent(SplashActivity.this, UserNavDrawer.class);//if login

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        intent = new Intent(SplashActivity.this, UserNavDrawer.class);//if login
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);//if not login

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                startActivity(intent);
                finish();
            }
        }, 4000);

    }
}