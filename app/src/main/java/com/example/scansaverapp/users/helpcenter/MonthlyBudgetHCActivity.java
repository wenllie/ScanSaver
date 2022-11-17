package com.example.scansaverapp.users.helpcenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;

import com.example.scansaverapp.R;

public class MonthlyBudgetHCActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView fromMonthlyBudgetToHelpCenter;
    LinearLayoutCompat budgetSavedYesterdayLayout, rangeOfBudgetLayout, chooseItemToFitBudgetLayout;
    LinearLayoutCompat budgetSavedYesterdayExpanded, rangeOfBudgetExpanded, chooseItemToFitBudgetExpanded;
    AppCompatImageView budgetSavedYesterdayArrow, rangeOfBudgetArrow, chooseItemToFitBudgetArrow;
    CardView monthlyBudgetIssueCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_budget_hcactivity);

        fromMonthlyBudgetToHelpCenter = findViewById(R.id.fromMonthlyBudgetToHelpCenter);
        monthlyBudgetIssueCardView = findViewById(R.id.monthlyBudgetIssueCardView);
        budgetSavedYesterdayLayout = findViewById(R.id.budgetSavedYesterdayLayout);
        rangeOfBudgetLayout = findViewById(R.id.rangeOfBudgetLayout);
        chooseItemToFitBudgetLayout = findViewById(R.id.chooseItemToFitBudgetLayout);
        budgetSavedYesterdayExpanded = findViewById(R.id.budgetSavedYesterdayExpanded);
        rangeOfBudgetExpanded = findViewById(R.id.rangeOfBudgetExpanded);
        chooseItemToFitBudgetExpanded = findViewById(R.id.chooseItemToFitBudgetExpanded);
        budgetSavedYesterdayArrow = findViewById(R.id.budgetSavedYesterdayArrow);
        rangeOfBudgetArrow = findViewById(R.id.rangeOfBudgetArrow);
        chooseItemToFitBudgetArrow = findViewById(R.id.chooseItemToFitBudgetArrow);

        fromMonthlyBudgetToHelpCenter.setOnClickListener(this);
        budgetSavedYesterdayLayout.setOnClickListener(this);
        rangeOfBudgetLayout.setOnClickListener(this);
        chooseItemToFitBudgetLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toHelpCenter = new Intent(MonthlyBudgetHCActivity.this, HelpCenterActivity.class);
        toHelpCenter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toHelpCenter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toHelpCenter);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fromMonthlyBudgetToHelpCenter:
                onBackPressed();
                break;

            case R.id.budgetSavedYesterdayLayout:
                // If the CardView is already expanded, set its visibility
                // to gone
                if (budgetSavedYesterdayExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    budgetSavedYesterdayExpanded.setVisibility(View.GONE);
                    budgetSavedYesterdayArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    budgetSavedYesterdayExpanded.setVisibility(View.VISIBLE);
                    budgetSavedYesterdayArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.rangeOfBudgetLayout:
                // If the CardView is already expanded, set its visibility
                // to gone
                if (rangeOfBudgetExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    rangeOfBudgetExpanded.setVisibility(View.GONE);
                    rangeOfBudgetArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    rangeOfBudgetExpanded.setVisibility(View.VISIBLE);
                    rangeOfBudgetArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.chooseItemToFitBudgetLayout:
                // If the CardView is already expanded, set its visibility
                // to gone
                if (chooseItemToFitBudgetExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    chooseItemToFitBudgetExpanded.setVisibility(View.GONE);
                    chooseItemToFitBudgetArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(monthlyBudgetIssueCardView, new AutoTransition());
                    chooseItemToFitBudgetExpanded.setVisibility(View.VISIBLE);
                    chooseItemToFitBudgetArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

        }
    }
}