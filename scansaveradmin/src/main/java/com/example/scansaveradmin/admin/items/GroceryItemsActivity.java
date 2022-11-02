package com.example.scansaveradmin.admin.items;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaveradmin.AdminNavDrawerActivity;
import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.customers.CustomersActivity;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListAdapter;
import com.example.scansaveradmin.admin.customers.customerlist.CustomerListModel;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListAdapter;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListModel;
import com.example.scansaveradmin.admin.settings.SettingsActivity;
import com.example.scansaveradmin.databinding.ActivityAddGroceryItemBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroceryItemsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frGroceryToDashboard;

    //grocery list
    RecyclerView groceryListRecycler;
    List<GroceryListModel> groceryList;
    GroceryListAdapter groceryListAdapter;

    //search
    SearchView searchGroceryItems;

    //sort
    AppCompatImageView sortItemsBtn;

    //floating button
    FloatingActionButton addItemFloatingBtn;
    Animation floatingBtnAnim;

    //firebase
    public static String userID;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_items);

        frGroceryToDashboard = findViewById(R.id.frGroceryToDashboard);
        groceryListRecycler = findViewById(R.id.groceryListRecycler);
        addItemFloatingBtn = findViewById(R.id.addItemFloatingBtn);
        searchGroceryItems = findViewById(R.id.searchGroceryItems);
        sortItemsBtn = findViewById(R.id.sortItemsBtn);

        //floating button
        floatingBtnAnim = AnimationUtils.loadAnimation(GroceryItemsActivity.this, R.anim.floating_action_add_anim);

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

                    for (DataSnapshot categorySnap : groceryId.getChildren()) {

                        for (DataSnapshot snap : categorySnap.getChildren()) {

                            GroceryListModel groceryListModel = snap.getValue(GroceryListModel.class);
                            groceryList.add(groceryListModel);
                            groceryListAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroceryItemsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //search view
        searchGroceryItems.clearFocus();
        searchGroceryItems.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterGroceryList(newText);
                return true;
            }
        });

        //set on click listeners
        frGroceryToDashboard.setOnClickListener(this);
        addItemFloatingBtn.setOnClickListener(this);
        sortItemsBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(GroceryItemsActivity.this, AdminNavDrawerActivity.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frGroceryToDashboard:
                onBackPressed();
                break;
            case R.id.addItemFloatingBtn:
                addItemFloatingBtn.setAnimation(floatingBtnAnim);
                startActivity(new Intent(GroceryItemsActivity.this, AddGroceryItemActivity.class));
                finish();
                break;

            case R.id.sortItemsBtn:
                SortGroceryItems();
                break;
        }
    }

    private void filterGroceryList(String newText) {

        List<GroceryListModel> filteredList = new ArrayList<>();

        for (GroceryListModel groceryListModel : groceryList) {

            if (groceryListModel.getGroceryName().toLowerCase().contains(newText.toLowerCase())) {

                filteredList.add(groceryListModel);

            }
            groceryListAdapter.filteredGroceryList(filteredList);
        }

    }

    private void SortGroceryItems() {

        final DialogPlus sortDialog = DialogPlus.newDialog(GroceryItemsActivity.this)
                .setContentHolder(new ViewHolder(R.layout.pop_up_sort_grocery_items))
                .setExpanded(true, 1500)
                .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                .create();

        View v = sortDialog.getHolderView();

        AppCompatTextView frozenFoodsCategory = v.findViewById(R.id.frozenFoodsCategory);

    }
}