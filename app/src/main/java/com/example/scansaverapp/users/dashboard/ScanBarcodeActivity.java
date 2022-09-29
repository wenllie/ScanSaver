package com.example.scansaverapp.users.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.users.dashboard.barcodedb.GroceryItemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ScanBarcodeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frScannerToDashboard;
    //    AppCompatImageView scannerOpenCameraBtn;
    AppCompatButton scannerBarcodeBtn, scannerViewCartBtn;
    private String TAG = "BarcodeActivity";

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

        scannerBarcodeBtn = (AppCompatButton) findViewById(R.id.scannerBarcodeBtn);
        scannerViewCartBtn = (AppCompatButton) findViewById(R.id.scannerViewCartBtn);
//        scannerOpenCameraBtn = (AppCompatImageView) findViewById(R.id.scannerOpenCameraBtn);
        frScannerToDashboard = (ImageView) findViewById(R.id.frScannerToDashboard);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        scannerBarcodeBtn.setOnClickListener(this);
        scannerViewCartBtn.setOnClickListener(this);
        frScannerToDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scannerBarcodeBtn:
                scanCode();
                break;
            case R.id.scannerViewCartBtn:
                Intent cart = new Intent(ScanBarcodeActivity.this, ShoppingCartViewActivity.class);
                startActivity(cart);
                break;
            case R.id.frScannerToDashboard:
                super.onBackPressed();
                break;
        }
    }

    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(ScanBarcodeActivity.this);

        intentIntegrator.setPrompt("Use volume up key to open flash");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.initiateScan();
        Intent intent = intentIntegrator.createScanIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        String groceryName, groceryMeasurement, groceryPrice, groceryCategory, groceryBrand, groceryUPCA, groceryEAN13;

        if (intentResult != null) {
            if (intentResult.getContents() != null) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getApplicationContext(), R.style.CutShapeTheme);

                DatabaseReference groceryItemReference = FirebaseDatabase.getInstance().getReference("Admin");

                groceryItemReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        currentDate = dateFormat.format(calendar.getTime());

                        for (DataSnapshot idSnapshot : snapshot.getChildren()) {

                            if (idSnapshot.child("Grocery Items").getChildren() != null) {

                                DataSnapshot groceryId = idSnapshot.child("Grocery Items");
                                for (DataSnapshot snap : groceryId.getChildren()) {

                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                    groceryItemModel.setGroceryQuantity("1");
                                    groceryItemModel.setGroceryDate(currentDate);


                                    String groceryName = groceryItemModel.getGroceryName();
                                    String groceryMeasurement = groceryItemModel.getGroceryMeasurement();
                                    String groceryPrice = groceryItemModel.getGroceryPrice();
                                    String groceryBrand = groceryItemModel.getGroceryBrand();
                                    String groceryCategory = groceryItemModel.getGroceryCategory();
                                    String groceryUPCA = groceryItemModel.getGroceryUPCA();
                                    String groceryEAN13 = groceryItemModel.getGroceryEAN13();
                                    String groceryDate = groceryItemModel.getGroceryDate();
                                    String groceryQuantity = groceryItemModel.getGroceryQuantity();

                                    builder.setTitle("Scan Complete!");
                                    builder.setMessage("You scanned " + "\n" + groceryName + "\n" +
                                            groceryMeasurement + groceryPrice + "\n\nIs this correct?");

                                    builder.setPositiveButton("Add to cart", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //store grocery details into realtime database
//                                            Log.i(TAG, "Barcode Number for getContents: " + intentResult.getContents());
//                                            Log.i(TAG, "Barcode Number for getUPC: " + groceryItemModel.getGroceryUPCA());

                                            DatabaseReference groceryReference = FirebaseDatabase.getInstance().getReference("Users");

                                            //creating 17 characters id for shopping cart
                                            String alphabetNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
                                            StringBuilder stringBuilder = new StringBuilder();
                                            Random rnd = new Random();
                                            while (stringBuilder.length() < 18) { // length of the random string.
                                                int index = (int) (rnd.nextFloat() * alphabetNum.length());
                                                stringBuilder.append(alphabetNum.charAt(index));
                                            }
                                            String itemID = stringBuilder.toString();

                                            //creating 17 characters id for grocery items
                                            String alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
                                            StringBuilder builder = new StringBuilder();
                                            Random random = new Random();
                                            while (builder.length() < 18) { // length of the random string.
                                                int index = (int) (random.nextFloat() * alphaNum.length());
                                                builder.append(alphaNum.charAt(index));
                                            }
                                            String groceryId = builder.toString();

                                            groceryReference.child(userID).child("Shopping Cart").child(currentDate).setValue(groceryItemModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ScanBarcodeActivity.this, "Added Successfully!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ScanBarcodeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            scanCode();
                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ScanBarcodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//
////                AlertDialog dialog = builder.create();
//                dialog.show();
            } else {
                Toast.makeText(this, "No Results Found", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}