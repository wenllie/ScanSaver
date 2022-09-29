package com.example.scansaverapp.users.settings;

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

import com.example.scansaverapp.R;

public class PrivacySecurityActivity extends AppCompatActivity implements View.OnClickListener{

    //declaring the variables
    ImageView frPrivacyToSettings;
    RelativeLayout termsConditionsDialogBtn, privacyPolicyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_security);

        //initializing the variables
        frPrivacyToSettings = (ImageView) findViewById(R.id.frPrivacyToSettings);
        termsConditionsDialogBtn = (RelativeLayout) findViewById(R.id.termsConditionsDialogBtn);
        privacyPolicyBtn = (RelativeLayout) findViewById(R.id.privacyPolicyBtn);

        frPrivacyToSettings.setOnClickListener(this);
        termsConditionsDialogBtn.setOnClickListener(this);
        privacyPolicyBtn.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frPrivacyToSettings:
                startActivity(new Intent(PrivacySecurityActivity.this, SettingsActivity.class));
                break;
            case R.id.termsConditionsDialogBtn:
                showDialogTerms();
                break;
            case R.id.privacyPolicyBtn:

                //Open dialog box for the privacy policies
                Dialog privacyDialog = new Dialog(this);
                privacyDialog.setContentView(R.layout.dialog_box_privacy_policies);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(privacyDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                privacyDialog.show();
                privacyDialog.getWindow().setAttributes(lp);
                privacyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showDialogTerms() {

        //open dialog box for the terms and conditions
        Dialog termsDialog = new Dialog(this);
        termsDialog.setContentView(R.layout.dialog_box_terms_and_conditions);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(termsDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        termsDialog.show();
        termsDialog.getWindow().setAttributes(lp);
        termsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

    }
}