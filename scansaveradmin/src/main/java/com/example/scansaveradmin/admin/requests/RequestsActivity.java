package com.example.scansaveradmin.admin.requests;

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
import com.example.scansaveradmin.admin.customers.CustomersActivity;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListModel;
import com.example.scansaveradmin.admin.items.GroceryItemsActivity;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListAdapter;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListModel;
import com.example.scansaveradmin.admin.requests.requestlist.RequestAdapter;
import com.example.scansaveradmin.admin.requests.requestlist.RequestListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frRequestsToDashboard;

    //request list
    RecyclerView recyclerRequests;
    List<RequestListModel> requestList;
    RequestAdapter requestAdapter;

    //firebase
    public static String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        frRequestsToDashboard = (ImageView) findViewById(R.id.frRequestsToDashboard);
        recyclerRequests = (RecyclerView) findViewById(R.id.recyclerRequests);

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //request list recycler
        recyclerRequests.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(getApplicationContext(), requestList);
        recyclerRequests.setAdapter(requestAdapter);
        DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Customers").child("Requests");
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                requestList.clear();

                for (DataSnapshot uidSnapshot : snapshot.getChildren()) {

                    RequestListModel requestListModel = uidSnapshot.getValue(RequestListModel.class);
                    requestList.add(requestListModel);
                    requestAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        frRequestsToDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frRequestsToDashboard:
                startActivity(new Intent(RequestsActivity.this, AdminNavDrawerActivity.class));
                break;
        }
    }
}