package com.example.scansaverapp.users.helpcenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.scansaverapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageAccountHCActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView fromManageAccountToHelpCenter;
    LinearLayoutCompat forgotPasswordLayout, changePasswordLayout, changeEmailLayout, changePhoneNumberLayout;
    LinearLayoutCompat forgotPasswordExpanded, changePasswordExpanded, changeEmailExpanded, changePhoneNumberExpanded;
    AppCompatImageView forgotPasswordArrow, changePasswordArrow, changeEmailArrow, changePhoneNumberArrow;
    CardView mainCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account_hc);

        mainCardView = findViewById(R.id.mainCardView);
        fromManageAccountToHelpCenter = findViewById(R.id.fromManageAccountToHelpCenter);
        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        changeEmailLayout = findViewById(R.id.changeEmailLayout);
        changePhoneNumberLayout = findViewById(R.id.changePhoneNumberLayout);
        forgotPasswordExpanded = findViewById(R.id.forgotPasswordExpanded);
        changePasswordExpanded = findViewById(R.id.changePasswordExpanded);
        changeEmailExpanded = findViewById(R.id.changeEmailExpanded);
        changePhoneNumberExpanded = findViewById(R.id.changePhoneNumberExpanded);
        forgotPasswordArrow = findViewById(R.id.forgotPasswordArrow);
        changePasswordArrow = findViewById(R.id.changePasswordArrow);
        changeEmailArrow = findViewById(R.id.changeEmailArrow);
        changePhoneNumberArrow = findViewById(R.id.changePhoneNumberArrow);

        fromManageAccountToHelpCenter.setOnClickListener(this);
        forgotPasswordLayout.setOnClickListener(this);
        changePasswordLayout.setOnClickListener(this);
        changeEmailLayout.setOnClickListener(this);
        changePhoneNumberLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toHelpCenter = new Intent(ManageAccountHCActivity.this, HelpCenterActivity.class);
        toHelpCenter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toHelpCenter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toHelpCenter);
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fromManageAccountToHelpCenter:
                onBackPressed();
                break;


            case R.id.forgotPasswordLayout:

                // If the CardView is already expanded, set its visibility
                // to gone
                if (forgotPasswordExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    forgotPasswordExpanded.setVisibility(View.GONE);
                    forgotPasswordArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    forgotPasswordExpanded.setVisibility(View.VISIBLE);
                    forgotPasswordArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.changePasswordLayout:

                // If the CardView is already expanded, set its visibility
                // to gone
                if (changePasswordExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changePasswordExpanded.setVisibility(View.GONE);
                    changePasswordArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changePasswordExpanded.setVisibility(View.VISIBLE);
                    changePasswordArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.changeEmailLayout:

                // If the CardView is already expanded, set its visibility
                // to gone
                if (changeEmailExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changeEmailExpanded.setVisibility(View.GONE);
                    changeEmailArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changeEmailExpanded.setVisibility(View.VISIBLE);
                    changeEmailArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.changePhoneNumberLayout:

                // If the CardView is already expanded, set its visibility
                // to gone
                if (changePhoneNumberExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changePhoneNumberExpanded.setVisibility(View.GONE);
                    changePhoneNumberArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    changePhoneNumberExpanded.setVisibility(View.VISIBLE);
                    changePhoneNumberArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

        }
    }
}