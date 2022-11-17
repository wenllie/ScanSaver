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

public class ShoppingCartHCActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView fromShoppingCartToHelpCenter;
    CardView shoppingCartIssueCardView;
    LinearLayoutCompat cantRemoveItemsLayout, notShowingOnCartLayout, barcodeCantBeReadLayout, generateReceiptAfterSavingLayout, havePaymentMethodLayout, deleteOptionForSpecificItemLayout;
    LinearLayoutCompat cantRemoveItemsExpanded, notShowingOnCartExpanded, barcodeCantBeReadExpanded, generateReceiptAfterSavingExpanded, havePaymentMethodExpanded, deleteOptionForSpecificItemExpanded;
    AppCompatImageView cantRemoveItemsArrow, notShowingOnCartArrow, barcodeCantBeReadArrow, generateReceiptAfterSavingArrow, havePaymentMethodArrow, deleteOptionForSpecificItemArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_hcactivity);

        fromShoppingCartToHelpCenter = findViewById(R.id.fromShoppingCartToHelpCenter);
        shoppingCartIssueCardView = findViewById(R.id.shoppingCartIssueCardView);
        cantRemoveItemsLayout = findViewById(R.id.cantRemoveItemsLayout);
        notShowingOnCartLayout = findViewById(R.id.notShowingOnCartLayout);
        barcodeCantBeReadLayout = findViewById(R.id.barcodeCantBeReadLayout);
        generateReceiptAfterSavingLayout = findViewById(R.id.generateReceiptAfterSavingLayout);
        havePaymentMethodLayout = findViewById(R.id.havePaymentMethodLayout);
        deleteOptionForSpecificItemLayout = findViewById(R.id.deleteOptionForSpecificItemLayout);
        cantRemoveItemsExpanded = findViewById(R.id.cantRemoveItemsExpanded);
        notShowingOnCartExpanded = findViewById(R.id.notShowingOnCartExpanded);
        barcodeCantBeReadExpanded = findViewById(R.id.barcodeCantBeReadExpanded);
        generateReceiptAfterSavingExpanded = findViewById(R.id.generateReceiptAfterSavingExpanded);
        havePaymentMethodExpanded = findViewById(R.id.havePaymentMethodExpanded);
        deleteOptionForSpecificItemExpanded = findViewById(R.id.deleteOptionForSpecificItemExpanded);
        cantRemoveItemsArrow = findViewById(R.id.cantRemoveItemsArrow);
        notShowingOnCartArrow = findViewById(R.id.notShowingOnCartArrow);
        barcodeCantBeReadArrow = findViewById(R.id.barcodeCantBeReadArrow);
        generateReceiptAfterSavingArrow = findViewById(R.id.generateReceiptAfterSavingArrow);
        havePaymentMethodArrow = findViewById(R.id.havePaymentMethodArrow);
        deleteOptionForSpecificItemArrow = findViewById(R.id.deleteOptionForSpecificItemArrow);

        fromShoppingCartToHelpCenter.setOnClickListener(this);
        cantRemoveItemsLayout.setOnClickListener(this);
        notShowingOnCartLayout.setOnClickListener(this);
        barcodeCantBeReadLayout.setOnClickListener(this);
        generateReceiptAfterSavingLayout.setOnClickListener(this);
        havePaymentMethodLayout.setOnClickListener(this);
        deleteOptionForSpecificItemLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toHelpCenter = new Intent(ShoppingCartHCActivity.this, HelpCenterActivity.class);
        toHelpCenter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toHelpCenter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toHelpCenter);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fromShoppingCartToHelpCenter:
                onBackPressed();
                break;

            case R.id.cantRemoveItemsLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (cantRemoveItemsExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    cantRemoveItemsExpanded.setVisibility(View.GONE);
                    cantRemoveItemsArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    cantRemoveItemsExpanded.setVisibility(View.VISIBLE);
                    cantRemoveItemsArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }

                break;

            case R.id.notShowingOnCartLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (notShowingOnCartExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    notShowingOnCartExpanded.setVisibility(View.GONE);
                    notShowingOnCartArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    notShowingOnCartExpanded.setVisibility(View.VISIBLE);
                    notShowingOnCartArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.barcodeCantBeReadLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (barcodeCantBeReadExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    barcodeCantBeReadExpanded.setVisibility(View.GONE);
                    barcodeCantBeReadArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    barcodeCantBeReadExpanded.setVisibility(View.VISIBLE);
                    barcodeCantBeReadArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.generateReceiptAfterSavingLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (generateReceiptAfterSavingExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    generateReceiptAfterSavingExpanded.setVisibility(View.GONE);
                    generateReceiptAfterSavingArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    generateReceiptAfterSavingExpanded.setVisibility(View.VISIBLE);
                    generateReceiptAfterSavingArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.havePaymentMethodLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (havePaymentMethodExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    havePaymentMethodExpanded.setVisibility(View.GONE);
                    havePaymentMethodArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    havePaymentMethodExpanded.setVisibility(View.VISIBLE);
                    havePaymentMethodArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;

            case R.id.deleteOptionForSpecificItemLayout:
                // If the CardView is already expanded, set its visibility to gone
                if (deleteOptionForSpecificItemExpanded.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    deleteOptionForSpecificItemExpanded.setVisibility(View.GONE);
                    deleteOptionForSpecificItemArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
                }

                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(shoppingCartIssueCardView, new AutoTransition());
                    deleteOptionForSpecificItemExpanded.setVisibility(View.VISIBLE);
                    deleteOptionForSpecificItemArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                }
                break;
        }
    }
}