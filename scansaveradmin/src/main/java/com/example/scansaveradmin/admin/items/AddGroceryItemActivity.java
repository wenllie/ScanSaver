package com.example.scansaveradmin.admin.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryItemDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class AddGroceryItemActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText groceryName, groceryMeasurement, groceryPrice, groceryBrand, groceryUPCA, groceryEAN13;
    AppCompatSpinner groceryCategorySpinner;
    AppCompatButton addGroceryItemBtn;

    FirebaseAuth mAuth;
    FirebaseUser user;
    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery_item);

        groceryName = (TextInputEditText) findViewById(R.id.groceryName);
        groceryMeasurement = (TextInputEditText) findViewById(R.id.groceryMeasurement);
        groceryPrice = (TextInputEditText) findViewById(R.id.groceryPrice);
        groceryBrand = (TextInputEditText) findViewById(R.id.groceryBrand);
        groceryUPCA = (TextInputEditText) findViewById(R.id.groceryUPCA);
        groceryEAN13 = (TextInputEditText) findViewById(R.id.groceryEAN13);
        groceryCategorySpinner = (AppCompatSpinner) findViewById(R.id.groceryCategorySpinner);
        addGroceryItemBtn = (AppCompatButton) findViewById(R.id.addGroceryItemBtn);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        addGroceryItemBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addGroceryItemBtn:
                addGroceryitem();
                break;
        }
    }

    private void addGroceryitem() {
        String name = groceryName.getText().toString();
        String measurement = groceryMeasurement.getText().toString();
        String price = groceryPrice.getText().toString();
        String brand = groceryBrand.getText().toString();
        String upca = groceryUPCA.getText().toString();
        String ean13 = groceryEAN13.getText().toString();
        String category = groceryCategorySpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            groceryName.setError("This field is required!");
            groceryName.requestFocus();
            return;
        }
        if (measurement.isEmpty()) {
            groceryMeasurement.setError("This field is required!");
            groceryMeasurement.requestFocus();
            return;
        }
        if (price.isEmpty()) {
            groceryPrice.setError("This field is required!");
            groceryPrice.requestFocus();
            return;
        }
        if (brand.isEmpty()) {
            groceryBrand.setError("This field is required!");
            groceryBrand.requestFocus();
            return;
        }
        if (upca.isEmpty()) {
            groceryUPCA.setError("This field is required!");
            groceryUPCA.requestFocus();
            return;
        }
        if (ean13.isEmpty()) {
            groceryEAN13.setError("This field is required!");
            groceryEAN13.requestFocus();
            return;
        }
        if (upca.length() != 12) {
            groceryUPCA.setError("UPC-A should contain 12 characters!");
            return;
        }
        if (ean13.length() != 14) {
            groceryEAN13.setError("EAN-13 should contain 14 characters!");
            return;
        }
        if (category.isEmpty()) {
            groceryCategorySpinner.requestFocus();
            return;
        } else {

            //store grocery details into realtime database
            GroceryItemDetail groceryItemDetail = new GroceryItemDetail(name, measurement, price, category, brand, upca, ean13);

            DatabaseReference groceryReference = FirebaseDatabase.getInstance().getReference("Admin");


            String itemID = upca;

            groceryReference.child("Grocery Items").child(itemID).setValue(groceryItemDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddGroceryItemActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                        clearField();
                    } else {
                        Toast.makeText(AddGroceryItemActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void clearField() {
        groceryName.setText("");
        groceryMeasurement.setText("");
        groceryPrice.setText("");
        groceryBrand.setText("");
        groceryUPCA.setText("");
        groceryEAN13.setText("");
        groceryCategorySpinner.setSelection(0);
        groceryName.clearFocus();
        groceryMeasurement.clearFocus();
        groceryPrice.clearFocus();
        groceryUPCA.clearFocus();
        groceryEAN13.clearFocus();
        groceryBrand.clearFocus();
    }
}