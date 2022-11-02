package com.example.scansaveradmin.admin.customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListModel;
import com.example.scansaveradmin.admin.settings.SettingsActivity;
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

    //Search view
    SearchView searchCustomers;

    public static String userID;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        frCustomersToDashboard = (ImageView) findViewById(R.id.frCustomersToDashboard);
        customerRecycler = (RecyclerView) findViewById(R.id.customerRecycler);
        searchCustomers = findViewById(R.id.searchCustomers);

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

        //search customers
        searchCustomers.clearFocus();
        searchCustomers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterCustomerList(newText);
                return true;
            }
        });

        //on click listeners
        frCustomersToDashboard.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(CustomersActivity.this, AdminNavDrawerActivity.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frCustomersToDashboard:
                onBackPressed();
                break;
        }
    }

    private void filterCustomerList(String newText) {

        List<CustomerListModel> filteredList = new ArrayList<>();

        for (CustomerListModel customerListModel : customerList) {

            if (customerListModel.getFullName().toLowerCase().contains(newText.toLowerCase())) {

                filteredList.add(customerListModel);

            }
            customerListAdapter.filteredCustomerList(filteredList);
        }

    }

}