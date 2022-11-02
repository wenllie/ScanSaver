package com.example.scansaverapp.users.spending;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.helpers_database.UserDetails;
import com.example.scansaverapp.users.dashboard.ShoppingCartViewActivity;
import com.example.scansaverapp.users.spending.spendingdb.CartNumberAdapter;
import com.example.scansaverapp.users.spending.spendingdb.CartNumberClass;
import com.example.scansaverapp.users.spending.spendingdb.MainCategorySpendingAdapter;
import com.example.scansaverapp.users.spending.spendingdb.MainCategorySpendingClass;
import com.example.scansaverapp.users.spending.spendingdb.MonthNameAdapter;
import com.example.scansaverapp.users.spending.spendingdb.MonthNameClass;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SpendingActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frSpendingToDashboard;
    AppCompatImageView spendingDownloadPDFBtn;
    Bitmap scaledBitmap, pdfBitmap;
    LinearLayoutCompat spendingLayout;
    //View spendingLayout;

    int pageWidth = 1200;
    private final int PICK_PDF_CODE = 2342;

    //show saved inventories
    RecyclerView spendingMainRecycler;
    List<GroceryItemModel> foodList;
    List<GroceryItemModel> beautyList;
    List<GroceryItemModel> homeList;
    List<GroceryItemModel> pharmacyList;
    List<MainCategorySpendingClass> categoryList;
    List<CartNumberClass> cartNumberList;
    List<MonthNameClass> monthNameList;

    //spending month
    Spinner chosenCategorySpinner, categorySpendingSpinner;
    ArrayList<String> categoryChosenList;

    private Calendar calendar;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    //database firebase
    public static String userID;
    private FirebaseUser user;
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Customers").child("Personal Information");
    private DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories");
    private UserDetails userDetails = new UserDetails();
    private GroceryItemModel groceryItemModel = new GroceryItemModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);

        frSpendingToDashboard = (ImageView) findViewById(R.id.frSpendingToDashboard);
        spendingMainRecycler = (RecyclerView) findViewById(R.id.spendingMainRecycler);
        spendingDownloadPDFBtn = (AppCompatImageView) findViewById(R.id.spendingDownloadPDFBtn);

        chosenCategorySpinner = findViewById(R.id.chosenCategorySpinner);
        categorySpendingSpinner = findViewById(R.id.categorySpendingSpinner);
        spendingLayout = findViewById(R.id.spendingLayout);

        //calendar
        calendar = Calendar.getInstance();
        int inventoryYear = calendar.get(Calendar.YEAR);
        String inventoryMonth = monthName[calendar.get(Calendar.MONTH)];

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //show spending per month and year
        categoryChosenList = new ArrayList<>();

        //show saved cart
        foodList = new ArrayList<>();
        beautyList = new ArrayList<>();
        homeList = new ArrayList<>();
        pharmacyList = new ArrayList<>();
        categoryList = new ArrayList<>();
        cartNumberList = new ArrayList<>();
        monthNameList = new ArrayList<>();

        DatabaseReference spendingCategoryReference = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories").child(userID);

        categorySpendingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (categorySpendingSpinner.getSelectedItemId() == 0) {
                    categoryChosenList.clear();

                    spendingCategoryReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            categoryChosenList.clear();

                            for (DataSnapshot snappy : snapshot.getChildren()) {

                                for (DataSnapshot snap : snappy.getChildren()) {

                                    String monthKey = snap.getKey();
                                    categoryChosenList.add(monthKey);

                                }
                            }

                            ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(SpendingActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryChosenList);

                            chosenCategorySpinner.setAdapter(monthAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    chosenCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            spendingCategoryReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    categoryList.clear();
                                    cartNumberList.clear();

                                    for (DataSnapshot yearSnap : snapshot.getChildren()) {

                                        for (DataSnapshot monthSnap : yearSnap.getChildren()) {

                                            String monthKey = monthSnap.getKey();

                                            if (monthKey.equalsIgnoreCase(categoryChosenList.get(position))) {
                                                cartNumberList.clear();

                                                for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                    String cartKey = dateSnap.getKey();

                                                    for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                        String catSnap = categorySnap.getKey();

                                                        if (catSnap.equalsIgnoreCase("Food")) {
                                                            foodList.clear();

                                                            for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                foodList.add(groceryItemModel);
                                                            }
                                                            categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                        } else if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                            beautyList.clear();

                                                            for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                beautyList.add(groceryItemModel);
                                                            }
                                                            categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                        } else if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                            homeList.clear();

                                                            for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                homeList.add(groceryItemModel);
                                                            }
                                                            categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                        } else if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                            pharmacyList.clear();

                                                            for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                pharmacyList.add(groceryItemModel);
                                                            }
                                                            categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                        }

                                                    }

                                                    cartNumberList.add(new CartNumberClass(cartKey, categoryList));
                                                }
                                            }


                                        }

                                    }

                                    CartNumberAdapter cartNumberAdapter = new CartNumberAdapter(cartNumberList, SpendingActivity.this);
                                    spendingMainRecycler.setLayoutManager(new LinearLayoutManager(SpendingActivity.this, LinearLayoutManager.VERTICAL, false));
                                    spendingMainRecycler.setAdapter(cartNumberAdapter);
                                    cartNumberAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } else {
                    categoryChosenList.clear();
                    categoryList.clear();
                    foodList.clear();
                    beautyList.clear();
                    homeList.clear();
                    pharmacyList.clear();
                    cartNumberList.clear();
                    monthNameList.clear();

                    spendingCategoryReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            categoryChosenList.clear();

                            for (DataSnapshot snappy : snapshot.getChildren()) {
                                String yearKey = snappy.getKey();
                                categoryChosenList.add(yearKey);
                            }

                            ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(SpendingActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryChosenList);

                            chosenCategorySpinner.setAdapter(yearAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    chosenCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                            spendingCategoryReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot yearSnap : snapshot.getChildren()) {

                                        String yearKey = yearSnap.getKey();

                                        if (yearKey.equalsIgnoreCase(categoryChosenList.get(position))) {

                                            for (DataSnapshot monthSnap : yearSnap.getChildren()) {

                                                String monthKey = monthSnap.getKey();

                                                if (monthKey.equalsIgnoreCase("January")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {
                                                        cartNumberList.clear();
                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("February")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("March")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("April")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("May")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("June")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("July")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("August")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("September")) {
                                                    cartNumberList.clear();

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {
                                                        categoryList.clear();

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("October")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {
                                                        categoryList.clear();

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("November")) {
                                                    cartNumberList.clear();

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {
                                                        categoryList.clear();

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                Toast.makeText(SpendingActivity.this, catSnap + String.valueOf(foodList), Toast.LENGTH_SHORT).show();

                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            } else if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                Toast.makeText(SpendingActivity.this, catSnap + String.valueOf(categorySnap.getValue()), Toast.LENGTH_SHORT).show();

                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            } else if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            } else if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                } else if (monthKey.equalsIgnoreCase("December")) {

                                                    for (DataSnapshot dateSnap : monthSnap.getChildren()) {

                                                        String cartNumKey = dateSnap.getKey();

                                                        for (DataSnapshot categorySnap : dateSnap.getChildren()) {

                                                            String catSnap = categorySnap.getKey();

                                                            if (catSnap.equalsIgnoreCase("Food")) {
                                                                foodList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {
                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    foodList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, foodList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                beautyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    beautyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, beautyList));
                                                            }

                                                            if (catSnap.equalsIgnoreCase("Home Essentials")) {
                                                                homeList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    homeList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, homeList));

                                                            }

                                                            if (catSnap.equalsIgnoreCase("Pharmacy")) {
                                                                pharmacyList.clear();

                                                                for (DataSnapshot snap : categorySnap.getChildren()) {

                                                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                                                    pharmacyList.add(groceryItemModel);
                                                                }
                                                                categoryList.add(new MainCategorySpendingClass(catSnap, pharmacyList));

                                                            }

                                                        }

                                                        cartNumberList.add(new CartNumberClass(cartNumKey, categoryList));

                                                    }

                                                    monthNameList.add(new MonthNameClass(monthKey, cartNumberList));

                                                }

                                            }
                                        }

                                    }

                                    MonthNameAdapter monthNameAdapter = new MonthNameAdapter(monthNameList, SpendingActivity.this);
                                    spendingMainRecycler.setLayoutManager(new LinearLayoutManager(SpendingActivity.this, LinearLayoutManager.VERTICAL, false));
                                    spendingMainRecycler.setAdapter(monthNameAdapter);
                                    monthNameAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SpendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //on click buttons
        frSpendingToDashboard.setOnClickListener(this);
        spendingDownloadPDFBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(SpendingActivity.this, UserNavDrawer.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frSpendingToDashboard:
                onBackPressed();
                break;

            case R.id.spendingDownloadPDFBtn:

                calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy-HH.mm.ss");
                String currentDate = dateFormat.format(calendar.getTime());

                MaterialAlertDialogBuilder pdfDialog = new MaterialAlertDialogBuilder(SpendingActivity.this, R.style.CutShapeTheme);
                pdfDialog.setTitle("Download PDF");
                pdfDialog.setMessage("Is your theme set to a light theme?\n\nNote: Make sure that your theme is in \"Light Theme\" before downloading PDF.");

                pdfDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final ProgressDialog progressDialog = new ProgressDialog(SpendingActivity.this);
                                progressDialog.setTitle("Downloading");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                PdfGenerator.getBuilder()
                                        .setContext(SpendingActivity.this)
                                        .fromViewIDSource()
                                        .fromViewID(SpendingActivity.this, R.id.spendingLayout)
                                        .setFileName(currentDate)
                                        .setFolderNameOrPath("ScanSaver/")
                                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                                        .build(new PdfGeneratorListener() {
                                            @Override
                                            public void onFailure(FailureResponse failureResponse) {
                                                super.onFailure(failureResponse);
                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void onStartPDFGeneration() {
                                                progressDialog.setMessage("Your PDF file is downloading.");

                                            }

                                            @Override
                                            public void onFinishPDFGeneration() {
                                                progressDialog.dismiss();
                                            }

                                            @Override
                                            public void onSuccess(SuccessResponse response) {
                                                super.onSuccess(response);
                                                progressDialog.dismiss();
                                                Toast.makeText(SpendingActivity.this, "Created successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                pdfDialog.show();

                break;
        }
    }

}