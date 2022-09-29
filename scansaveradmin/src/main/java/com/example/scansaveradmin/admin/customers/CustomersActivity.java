package com.example.scansaveradmin.admin.customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaveradmin.AdminNavDrawerActivity;
import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListAdapter;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView customerRecycler;
    ImageView frCustomersToDashboard;

    //Customer List
    List<CustomerListModel> customerList;
    CustomerListAdapter customerListAdapter;

    public static String userID;

    private FirebaseUser user;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(CustomersActivity.this, AdminNavDrawerActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        frCustomersToDashboard = (ImageView) findViewById(R.id.frCustomersToDashboard);
        customerRecycler = (RecyclerView) findViewById(R.id.customerRecycler);

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //show customer details in the recycler view
        customerRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        customerList = new ArrayList<>();
        customerListAdapter = new CustomerListAdapter(getApplicationContext(), customerList);
        customerRecycler.setAdapter(customerListAdapter);

        DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference("Customers").child("Personal Information");
        customerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                customerList.clear();

                for (DataSnapshot uidSnapshot : snapshot.getChildren()) {

                    CustomerListModel customerListModel = uidSnapshot.getValue(CustomerListModel.class);
                    customerList.add(customerListModel);
                    customerListAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomersActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //on click listeners
        frCustomersToDashboard.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frCustomersToDashboard:
                startActivity(new Intent(CustomersActivity.this, AdminNavDrawerActivity.class));
                overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim);
                break;
        }
    }
}