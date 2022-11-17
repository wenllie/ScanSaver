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

public class GrocerySpendingHCActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView fromGrocerySpendingToHelpCenter;
    LinearLayoutCompat keepTrackOnGrocerySpendingLayout, stepsInGettingSpendingLayout, howMuchSaveMonthlyLayout, howMuchSaveYearlyLayout;
    LinearLayoutCompat keepTrackOnGrocerySpendingExpanded, stepsInGettingSpendingExpanded, howMuchSaveMonthlyExpanded, howMuchSaveYearlyExpanded;
    AppCompatImageView keepTrackOnGrocerySpendingArrow, stepsInGettingSpendingArrow, howMuchSaveMonthlyArrow, howMuchSaveYearlyArrow;
    CardView grocerySpendingCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_spending_hcactivity);

        fromGrocerySpendingToHelpCenter = findViewById(R.id.fromGrocerySpendingToHelpCenter);
        grocerySpendingCardView = findViewById(R.id.grocerySpendingCardView);
        keepTrackOnGrocerySpendingLayout = findViewById(R.id.keepTrackOnGrocerySpendingLayout);
        stepsInGettingSpendingLayout = findViewById(R.id.stepsInGettingSpendingLayout);
        howMuchSaveMonthlyLayout = findViewById(R.id.howMuchSaveMonthlyLayout);
        howMuchSaveYearlyLayout = findViewById(R.id.howMuchSaveYearlyLayout);
        keepTrackOnGrocerySpendingExpanded = findViewById(R.id.keepTrackOnGrocerySpendingExpanded);
        stepsInGettingSpendingExpanded = findViewById(R.id.stepsInGettingSpendingExpanded);
        howMuchSaveMonthlyExpanded = findViewById(R.id.howMuchSaveMonthlyExpanded);
        howMuchSaveYearlyExpanded = findViewById(R.id.howMuchSaveYearlyExpanded);
        keepTrackOnGrocerySpendingArrow = findViewById(R.id.keepTrackOnGrocerySpendingArrow);
        stepsInGettingSpendingArrow = findViewById(R.id.stepsInGettingSpendingArrow);
        howMuchSaveMonthlyArrow = findViewById(R.id.howMuchSaveMonthlyArrow);
        howMuchSaveYearlyArrow = findViewById(R.id.howMuchSaveYearlyArrow);

        fromGrocerySpendingToHelpCenter.setOnClickListener(this);
        keepTrackOnGrocerySpendingLayout.setOnClickListener(this);
        stepsInGettingSpendingLayout.setOnClickListener(this);
        howMuchSaveMonthlyLayout.setOnClickListener(this);
        howMuchSaveYearlyLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toHelpCenter = new Intent(GrocerySpendingHCActivity.this, HelpCenterActivity.class);
        toHelpCenter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toHelpCenter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toHelpCenter);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fromGrocerySpendingToHelpCenter:
                onBackPressed();
                break;

            case R.id.keepTrackOnGrocerySpendingLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (keepTrackOnGrocerySpendingExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    keepTrackOnGrocerySpendingExpanded.setVisibility(View.GONE);
                    keepTrackOnGrocerySpendingArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    keepTrackOnGrocerySpendingExpanded.setVisibility(View.VISIBLE);
                    keepTrackOnGrocerySpendingArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.stepsInGettingSpendingLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (stepsInGettingSpendingExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    stepsInGettingSpendingExpanded.setVisibility(View.GONE);
                    stepsInGettingSpendingArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    stepsInGettingSpendingExpanded.setVisibility(View.VISIBLE);
                    stepsInGettingSpendingArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.howMuchSaveMonthlyLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (howMuchSaveMonthlyExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    howMuchSaveMonthlyExpanded.setVisibility(View.GONE);
                    howMuchSaveMonthlyArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    howMuchSaveMonthlyExpanded.setVisibility(View.VISIBLE);
                    howMuchSaveMonthlyArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.howMuchSaveYearlyLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (howMuchSaveYearlyExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    howMuchSaveYearlyExpanded.setVisibility(View.GONE);
                    howMuchSaveYearlyArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(grocerySpendingCardView, new AutoTransition());
                    howMuchSaveYearlyExpanded.setVisibility(View.VISIBLE);
                    howMuchSaveYearlyArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

        }
    }
}