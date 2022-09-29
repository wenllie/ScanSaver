package com.example.scansaveradmin.admin.complaints;

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
import com.example.scansaveradmin.admin.complaints.complaintlist.ComplaintAdapter;
import com.example.scansaveradmin.admin.complaints.complaintlist.ComplaintListModel;
import com.example.scansaveradmin.admin.requests.RequestsActivity;
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

public class ComplaintsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView frComplaintsToDashboard;

    //complaint list
    RecyclerView recyclerComplaints;
    List<ComplaintListModel> complaintList;
    ComplaintAdapter complaintAdapter;

    //firebase
    public static String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        frComplaintsToDashboard = (ImageView) findViewById(R.id.frComplaintsToDashboard);
        recyclerComplaints = (RecyclerView) findViewById(R.id.recyclerComplaints);

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //complaint list recycler
        recyclerComplaints.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        complaintList = new ArrayList<>();
        complaintAdapter = new ComplaintAdapter(getApplicationContext(), complaintList);
        recyclerComplaints.setAdapter(complaintAdapter);
        DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Customers").child("Complaints");
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                complaintList.clear();

                for (DataSnapshot uidSnapshot : snapshot.getChildren()) {

                    ComplaintListModel complaintListModel = uidSnapshot.getValue(ComplaintListModel.class);
                    complaintList.add(complaintListModel);
                    complaintAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ComplaintsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        frComplaintsToDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frComplaintsToDashboard:
                startActivity(new Intent(ComplaintsActivity.this, AdminNavDrawerActivity.class));
                break;
        }
    }
}