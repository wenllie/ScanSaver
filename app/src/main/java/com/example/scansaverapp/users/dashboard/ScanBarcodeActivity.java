package com.example.scansaverapp.users.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.users.helpcenter.HelpCenterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScanBarcodeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frScannerToDashboard;
    RelativeLayout scannerBarcodeBtn, shoppingCartBtn;


    FirebaseAuth mAuth;
    FirebaseUser user;
    public static String userID;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        scannerBarcodeBtn = findViewById(R.id.scannerBarcodeBtn);
        shoppingCartBtn = findViewById(R.id.shoppingCartBtn);
        frScannerToDashboard = (ImageView) findViewById(R.id.frScannerToDashboard);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        scannerBarcodeBtn.setOnClickListener(this);
        shoppingCartBtn.setOnClickListener(this);
        frScannerToDashboard.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(ScanBarcodeActivity.this, UserNavDrawer.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scannerBarcodeBtn:
                scanCode();
                break;
            case R.id.frScannerToDashboard:
                onBackPressed();
                break;
            case R.id.shoppingCartBtn:
                startActivity(new Intent(ScanBarcodeActivity.this, ShoppingCartViewActivity.class));
                finish();
                break;
        }
    }

    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(ScanBarcodeActivity.this);

        intentIntegrator.setPrompt("Use volume up key to open flash");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
//        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.UPC_A);
        intentIntegrator.initiateScan();
        Intent intent = intentIntegrator.createScanIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        DatabaseReference groceryItemReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");

        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                if (intentResult.getFormatName().equals(IntentIntegrator.UPC_A)) {

                    String upcaIntent = intentResult.getContents();

                    groceryItemReference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot categorySnap : snapshot.getChildren()) {

                                String categoryKey = categorySnap.getKey();

                                if (categoryKey.equalsIgnoreCase("Food")) {

                                    for (DataSnapshot snap : categorySnap.getChildren()) {

                                        String UPCAKey = snap.getKey();

                                        if (upcaIntent.equals(UPCAKey)) {

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            LayoutInflater layoutInflater = LayoutInflater.from(ScanBarcodeActivity.this);
                                            View view = layoutInflater.inflate(R.layout.card_view_show_scanned, null);

                                            AppCompatButton addToCartBtn = view.findViewById(R.id.addToCartBtn);
                                            AppCompatButton cancelAddCartBtn = view.findViewById(R.id.cancelAddCartBtn);
                                            AppCompatTextView groceryName = view.findViewById(R.id.groceryName);
                                            AppCompatTextView groceryMeasurement = view.findViewById(R.id.groceryMeasurement);
                                            AppCompatTextView groceryPrice = view.findViewById(R.id.groceryPrice);
                                            AppCompatImageView groceryPhoto = view.findViewById(R.id.groceryPhoto);

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            groceryName.setText(snap.child("groceryName").getValue().toString());
                                            groceryMeasurement.setText(snap.child("groceryMeasurement").getValue().toString());
                                            groceryPrice.setText(snap.child("groceryPrice").getValue().toString());

                                            Glide.with(groceryPhoto.getContext())
                                                    .load(snap.child("groceryImgUrl").getValue())
                                                    .into(groceryPhoto);

                                            AlertDialog showItemAlertDialog = new AlertDialog.Builder(ScanBarcodeActivity.this)
                                                    .setView(view)
                                                    .create();

                                            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    String name = snap.child("groceryName").getValue().toString();
                                                    String measurement = snap.child("groceryMeasurement").getValue().toString();
                                                    String price = snap.child("groceryPrice").getValue().toString();
                                                    String category = snap.child("groceryCategory").getValue().toString();
                                                    String brand = snap.child("groceryBrand").getValue().toString();
                                                    String upca = snap.child("groceryUPCA").getValue().toString();
                                                    String ean13 = snap.child("groceryEAN13").getValue().toString();
                                                    String quantity = "1";
                                                    String date = currentDate;
                                                    String customerId = FirebaseAuth.getInstance().getUid();
                                                    String totalPrice = snap.child("groceryPrice").getValue().toString();
                                                    String groceryImgUrl = snap.child("groceryImgUrl").getValue().toString();

                                                    DatabaseReference addToCartReference = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart").child(FirebaseAuth.getInstance().getUid());

                                                    addToCartReference.child(categoryKey).child(upca).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").push();

                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").setValue(name);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").setValue(measurement);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").setValue(price);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").setValue(category);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").setValue(brand);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").setValue(upca);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").setValue(ean13);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").setValue(quantity);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").setValue(date);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").setValue(totalPrice);
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").setValue(customerId);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").setValue(groceryImgUrl);

                                                                Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ScanBarcodeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                    showItemAlertDialog.dismiss();
                                                }
                                            });
                                            cancelAddCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    showItemAlertDialog.cancel();
                                                }
                                            });
                                            showItemAlertDialog.show();
                                            showItemAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                                        } else {

                                        }

                                    }

                                } else if(categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                    for (DataSnapshot snap : categorySnap.getChildren()) {

                                        String UPCAKey = snap.getKey();

                                        if (upcaIntent.equals(UPCAKey)) {

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            LayoutInflater layoutInflater = LayoutInflater.from(ScanBarcodeActivity.this);
                                            View view = layoutInflater.inflate(R.layout.card_view_show_scanned, null);

                                            AppCompatButton addToCartBtn = view.findViewById(R.id.addToCartBtn);
                                            AppCompatButton cancelAddCartBtn = view.findViewById(R.id.cancelAddCartBtn);
                                            AppCompatTextView groceryName = view.findViewById(R.id.groceryName);
                                            AppCompatTextView groceryMeasurement = view.findViewById(R.id.groceryMeasurement);
                                            AppCompatTextView groceryPrice = view.findViewById(R.id.groceryPrice);
                                            AppCompatImageView groceryPhoto = view.findViewById(R.id.groceryPhoto);

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            groceryName.setText(snap.child("groceryName").getValue().toString());
                                            groceryMeasurement.setText(snap.child("groceryMeasurement").getValue().toString());
                                            groceryPrice.setText(snap.child("groceryPrice").getValue().toString());

                                            Glide.with(groceryPhoto.getContext())
                                                    .load(snap.child("groceryImgUrl").getValue())
                                                    .into(groceryPhoto);

                                            AlertDialog showItemAlertDialog = new AlertDialog.Builder(ScanBarcodeActivity.this)
                                                    .setView(view)
                                                    .create();

                                            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    String name = snap.child("groceryName").getValue().toString();
                                                    String measurement = snap.child("groceryMeasurement").getValue().toString();
                                                    String price = snap.child("groceryPrice").getValue().toString();
                                                    String category = snap.child("groceryCategory").getValue().toString();
                                                    String brand = snap.child("groceryBrand").getValue().toString();
                                                    String upca = snap.child("groceryUPCA").getValue().toString();
                                                    String ean13 = snap.child("groceryEAN13").getValue().toString();
                                                    String quantity = "1";
                                                    String date = currentDate;
                                                    String customerId = FirebaseAuth.getInstance().getUid();
                                                    String totalPrice = snap.child("groceryPrice").getValue().toString();
                                                    String groceryImgUrl = snap.child("groceryImgUrl").getValue().toString();

                                                    DatabaseReference addToCartReference = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart").child(FirebaseAuth.getInstance().getUid());

                                                    addToCartReference.child(categoryKey).child(upca).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").push();

                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").setValue(name);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").setValue(measurement);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").setValue(price);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").setValue(category);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").setValue(brand);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").setValue(upca);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").setValue(ean13);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").setValue(quantity);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").setValue(date);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").setValue(totalPrice);
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").setValue(customerId);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").setValue(groceryImgUrl);

                                                                Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ScanBarcodeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                    showItemAlertDialog.dismiss();
                                                }
                                            });
                                            cancelAddCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    showItemAlertDialog.cancel();
                                                }
                                            });
                                            showItemAlertDialog.show();
                                            showItemAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                                        } else {

                                        }

                                    }
                                } else if(categoryKey.equalsIgnoreCase("Home Essentials")) {

                                    for (DataSnapshot snap : categorySnap.getChildren()) {

                                        String UPCAKey = snap.getKey();

                                        if (upcaIntent.equals(UPCAKey)) {

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            LayoutInflater layoutInflater = LayoutInflater.from(ScanBarcodeActivity.this);
                                            View view = layoutInflater.inflate(R.layout.card_view_show_scanned, null);

                                            AppCompatButton addToCartBtn = view.findViewById(R.id.addToCartBtn);
                                            AppCompatButton cancelAddCartBtn = view.findViewById(R.id.cancelAddCartBtn);
                                            AppCompatTextView groceryName = view.findViewById(R.id.groceryName);
                                            AppCompatTextView groceryMeasurement = view.findViewById(R.id.groceryMeasurement);
                                            AppCompatTextView groceryPrice = view.findViewById(R.id.groceryPrice);
                                            AppCompatImageView groceryPhoto = view.findViewById(R.id.groceryPhoto);

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            groceryName.setText(snap.child("groceryName").getValue().toString());
                                            groceryMeasurement.setText(snap.child("groceryMeasurement").getValue().toString());
                                            groceryPrice.setText(snap.child("groceryPrice").getValue().toString());

                                            Glide.with(groceryPhoto.getContext())
                                                    .load(snap.child("groceryImgUrl").getValue())
                                                    .into(groceryPhoto);

                                            AlertDialog showItemAlertDialog = new AlertDialog.Builder(ScanBarcodeActivity.this)
                                                    .setView(view)
                                                    .create();

                                            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    String name = snap.child("groceryName").getValue().toString();
                                                    String measurement = snap.child("groceryMeasurement").getValue().toString();
                                                    String price = snap.child("groceryPrice").getValue().toString();
                                                    String category = snap.child("groceryCategory").getValue().toString();
                                                    String brand = snap.child("groceryBrand").getValue().toString();
                                                    String upca = snap.child("groceryUPCA").getValue().toString();
                                                    String ean13 = snap.child("groceryEAN13").getValue().toString();
                                                    String quantity = "1";
                                                    String date = currentDate;
                                                    String customerId = FirebaseAuth.getInstance().getUid();
                                                    String totalPrice = snap.child("groceryPrice").getValue().toString();
                                                    String groceryImgUrl = snap.child("groceryImgUrl").getValue().toString();

                                                    DatabaseReference addToCartReference = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart").child(FirebaseAuth.getInstance().getUid());

                                                    addToCartReference.child(categoryKey).child(upca).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").push();

                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").setValue(name);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").setValue(measurement);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").setValue(price);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").setValue(category);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").setValue(brand);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").setValue(upca);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").setValue(ean13);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").setValue(quantity);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").setValue(date);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").setValue(totalPrice);
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").setValue(customerId);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").setValue(groceryImgUrl);

                                                                Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ScanBarcodeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                    showItemAlertDialog.dismiss();
                                                }
                                            });
                                            cancelAddCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    showItemAlertDialog.cancel();
                                                }
                                            });
                                            showItemAlertDialog.show();
                                            showItemAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                                        } else {

                                        }

                                    }
                                } else if(categoryKey.equalsIgnoreCase("Pharmacy")) {

                                    for (DataSnapshot snap : categorySnap.getChildren()) {

                                        String UPCAKey = snap.getKey();

                                        if (upcaIntent.equals(UPCAKey)) {

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            LayoutInflater layoutInflater = LayoutInflater.from(ScanBarcodeActivity.this);
                                            View view = layoutInflater.inflate(R.layout.card_view_show_scanned, null);

                                            AppCompatButton addToCartBtn = view.findViewById(R.id.addToCartBtn);
                                            AppCompatButton cancelAddCartBtn = view.findViewById(R.id.cancelAddCartBtn);
                                            AppCompatTextView groceryName = view.findViewById(R.id.groceryName);
                                            AppCompatTextView groceryMeasurement = view.findViewById(R.id.groceryMeasurement);
                                            AppCompatTextView groceryPrice = view.findViewById(R.id.groceryPrice);
                                            AppCompatImageView groceryPhoto = view.findViewById(R.id.groceryPhoto);

                                            calendar = Calendar.getInstance();
                                            dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                                            currentDate = dateFormat.format(calendar.getTime());

                                            groceryName.setText(snap.child("groceryName").getValue().toString());
                                            groceryMeasurement.setText(snap.child("groceryMeasurement").getValue().toString());
                                            groceryPrice.setText(snap.child("groceryPrice").getValue().toString());

                                            Glide.with(groceryPhoto.getContext())
                                                    .load(snap.child("groceryImgUrl").getValue())
                                                    .into(groceryPhoto);

                                            AlertDialog showItemAlertDialog = new AlertDialog.Builder(ScanBarcodeActivity.this)
                                                    .setView(view)
                                                    .create();

                                            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    String name = snap.child("groceryName").getValue().toString();
                                                    String measurement = snap.child("groceryMeasurement").getValue().toString();
                                                    String price = snap.child("groceryPrice").getValue().toString();
                                                    String category = snap.child("groceryCategory").getValue().toString();
                                                    String brand = snap.child("groceryBrand").getValue().toString();
                                                    String upca = snap.child("groceryUPCA").getValue().toString();
                                                    String ean13 = snap.child("groceryEAN13").getValue().toString();
                                                    String quantity = "1";
                                                    String date = currentDate;
                                                    String customerId = FirebaseAuth.getInstance().getUid();
                                                    String totalPrice = snap.child("groceryPrice").getValue().toString();
                                                    String groceryImgUrl = snap.child("groceryImgUrl").getValue().toString();

                                                    DatabaseReference addToCartReference = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart").child(FirebaseAuth.getInstance().getUid());

                                                    addToCartReference.child(categoryKey).child(upca).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").push();
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").push();

                                                                addToCartReference.child(categoryKey).child(upca).child("groceryName").setValue(name);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryMeasurement").setValue(measurement);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryPrice").setValue(price);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryCategory").setValue(category);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryBrand").setValue(brand);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryUPCA").setValue(upca);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryEAN13").setValue(ean13);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryQuantity").setValue(quantity);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryDate").setValue(date);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryTotalItemPrice").setValue(totalPrice);
                                                                addToCartReference.child(categoryKey).child(upca).child("customerId").setValue(customerId);
                                                                addToCartReference.child(categoryKey).child(upca).child("groceryImgUrl").setValue(groceryImgUrl);

                                                                Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(ScanBarcodeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                    showItemAlertDialog.dismiss();
                                                }
                                            });
                                            cancelAddCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    showItemAlertDialog.cancel();
                                                }
                                            });
                                            showItemAlertDialog.show();
                                            showItemAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                                        } else {

                                        }

                                    }
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ScanBarcodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            } else {
                Toast.makeText(this, "No Results Found", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}