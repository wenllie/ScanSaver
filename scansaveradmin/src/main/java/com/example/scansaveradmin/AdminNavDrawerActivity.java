package com.example.scansaveradmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scansaveradmin.databinding.ActivityAdminNavDrawerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminNavDrawerActivity extends AppCompatActivity {


    TextView navFullName;
    CircleImageView navProfilePic;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAdminNavDrawerBinding binding;
    FirebaseAuth mAuth;
    private DatabaseReference adminReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminNavDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.appBarAdminNavDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        navFullName = navigationView.getHeaderView(0).findViewById(R.id.navFullName);
        navProfilePic = navigationView.getHeaderView(0).findViewById(R.id.navProfilePic);
        adminReference = FirebaseDatabase.getInstance().getReference().child("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        adminReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                navFullName.setText(snapshot.child("Personal Details").child("fullName").getValue().toString() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminNavDrawerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference profilePhotoReference = FirebaseDatabase.getInstance().getReference("Admin").child("Admin Users").child(FirebaseAuth.getInstance().getUid());

        profilePhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Picasso.get().load(snapshot.child("Profile Photo").child("profileUrl").getValue().toString()).into(navProfilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminNavDrawerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_customers, R.id.nav_admin_dashboard, R.id.nav_grocery_items, R.id.nav_complaints, R.id.nav_requests, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_nav_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setItemIconTintList(null);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    private void logout() {
        mAuth.signOut();
        Intent signout = new Intent(AdminNavDrawerActivity.this, MainActivity.class);//open login activity on successful logout
        signout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signout);
        finish();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_nav_drawer, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin_nav_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}