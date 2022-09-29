package com.example.scansaverapp.users.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.users.spending.SpendingActivity;

public class ShoppingCartViewActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView frCartToDashboard;
    AppCompatButton grocerySpendingBtn, saveShoppingCartBtn;
    TextView totalPriceText;
    RecyclerView groceryItemsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_view);

        frCartToDashboard = (ImageView) findViewById(R.id.frCartToDashboard);
        grocerySpendingBtn = (AppCompatButton) findViewById(R.id.grocerySpendingBtn);
        saveShoppingCartBtn = (AppCompatButton) findViewById(R.id.saveShoppingCartBtn);
        totalPriceText = (TextView) findViewById(R.id.totalPriceText);
        groceryItemsRecycler = (RecyclerView) findViewById(R.id.groceryItemsRecycler);

        frCartToDashboard.setOnClickListener(this);
        grocerySpendingBtn.setOnClickListener(this);
        saveShoppingCartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frCartToDashboard:
                startActivity(new Intent(ShoppingCartViewActivity.this, UserNavDrawer.class));
                break;
            case R.id.grocerySpendingBtn:
                startActivity(new Intent(ShoppingCartViewActivity.this, SpendingActivity.class));
                break;
            case R.id.saveShoppingCartBtn:
                viewShoppingCart();
                break;
        }
    }

    private void viewShoppingCart() {
    }
}