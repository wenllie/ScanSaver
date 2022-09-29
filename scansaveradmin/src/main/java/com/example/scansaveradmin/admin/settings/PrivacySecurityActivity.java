package com.example.scansaveradmin.admin.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.scansaveradmin.R;

public class PrivacySecurityActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frPrivacySecurityToSettings;
    RelativeLayout privacyPolicyLayout, termAndConditionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_security);

        frPrivacySecurityToSettings = (ImageView) findViewById(R.id.frPrivacySecurityToSettings);
        privacyPolicyLayout = (RelativeLayout) findViewById(R.id.privacyPolicyLayout);
        termAndConditionsLayout = (RelativeLayout) findViewById(R.id.termAndConditionsLayout);

        frPrivacySecurityToSettings.setOnClickListener(this);
        termAndConditionsLayout.setOnClickListener(this);
        privacyPolicyLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frPrivacySecurityToSettings:
                startActivity(new Intent(PrivacySecurityActivity.this, SettingsActivity.class));
                finish();
                break;
            case R.id.termAndConditionsLayout:
                termsAndConditionsShowDialog();
                break;
            case R.id.privacyPolicyLayout:
                privacyPolicyShowDialog();
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void privacyPolicyShowDialog() {

        Dialog aboutUsDialog = new Dialog(this);
        aboutUsDialog.setContentView(R.layout.dialog_box_privacy_policy);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(aboutUsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        aboutUsDialog.show();
        aboutUsDialog.getWindow().setAttributes(lp);
        aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    }

    @SuppressLint("ResourceAsColor")
    private void termsAndConditionsShowDialog() {

        Dialog aboutUsDialog = new Dialog(this);
        aboutUsDialog.setContentView(R.layout.dialog_box_terms_and_conditions);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(aboutUsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        aboutUsDialog.show();
        aboutUsDialog.getWindow().setAttributes(lp);
        aboutUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    }
}