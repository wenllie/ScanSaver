package com.example.scansaveradmin.admin.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaveradmin.AdminNavDrawerActivity;
import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.customers.CustomersActivity;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListAdapter;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListModel;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListAdapter;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroceryItemsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frGroceryToDashboard;
    AppCompatButton addGroceryItemBtn;

    //grocery list
    RecyclerView groceryListRecycler;
    List<GroceryListModel> groceryList;
    GroceryListAdapter groceryListAdapter;

    //firebase
    public static String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_items);

        frGroceryToDashboard = (ImageView) findViewById(R.id.frGroceryToDashboard);
        addGroceryItemBtn = (AppCompatButton) findViewById(R.id.addGroceryItemBtn);
        groceryListRecycler = (RecyclerView) findViewById(R.id.groceryListRecycler);

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //grocery list recycler
        groceryListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        groceryList = new ArrayList<>();
        groceryListAdapter = new GroceryListAdapter(getApplicationContext(), groceryList);
        groceryListRecycler.setAdapter(groceryListAdapter);
        DatabaseReference groceryReference = FirebaseDatabase.getInstance().getReference("Admin");
        groceryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                groceryList.clear();

                if (snapshot.child("Grocery Items").getChildren() != null) {
//                        Toast.makeText(GroceryItemsActivity.this, uidSnapshot.child("Grocery Items").getChildren().toString(), Toast.LENGTH_SHORT).show();

                    DataSnapshot groceryId = snapshot.child("Grocery Items");

                    for (DataSnapshot snap : groceryId.getChildren()) {
                        GroceryListModel groceryListModel = snap.getValue(GroceryListModel.class);
                        groceryList.add(groceryListModel);
                        groceryListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroceryItemsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //set on click listeners
        frGroceryToDashboard.setOnClickListener(this);
        addGroceryItemBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frGroceryToDashboard:
                startActivity(new Intent(GroceryItemsActivity.this, AdminNavDrawerActivity.class));
                finish();
                break;
            case R.id.addGroceryItemBtn:
                startActivity(new Intent(GroceryItemsActivity.this, AddGroceryItemActivity.class));
                finish();
                break;
        }
    }
}