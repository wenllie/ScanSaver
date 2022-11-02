package com.example.scansaveradmin.admin.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryItemDetail;
import com.example.scansaveradmin.admin.settings.ProfilePhotoModel;
import com.example.scansaveradmin.admin.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AddGroceryItemActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText groceryName, groceryMeasurement, groceryPrice, groceryBrand, groceryEAN13;
    AppCompatSpinner groceryCategorySpinner, grocerySubCategorySpinner;
    AppCompatButton addGroceryItemBtn, scanBarcodeBtn, groceryPhotoBtn;
    AppCompatImageView groceryPhotoShow;
    AppCompatTextView groceryUPCA;

    FirebaseAuth mAuth;
    FirebaseUser user;
    public static String userID;

    String[] beautyCategory, foodCategory, homeCategory, pharmacyCategory, mainCat;

    private FirebaseStorage firebaseStorage;
    private StorageReference imageStorageRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery_item);

        groceryName = (TextInputEditText) findViewById(R.id.groceryName);
        groceryMeasurement = (TextInputEditText) findViewById(R.id.groceryMeasurement);
        groceryPrice = (TextInputEditText) findViewById(R.id.groceryPrice);
        groceryBrand = (TextInputEditText) findViewById(R.id.groceryBrand);
        groceryUPCA = (AppCompatTextView) findViewById(R.id.groceryUPCA);
        groceryEAN13 = (TextInputEditText) findViewById(R.id.groceryEAN13);
        groceryCategorySpinner = (AppCompatSpinner) findViewById(R.id.groceryCategorySpinner);
        grocerySubCategorySpinner = (AppCompatSpinner) findViewById(R.id.grocerySubCategorySpinner);
        addGroceryItemBtn = (AppCompatButton) findViewById(R.id.addGroceryItemBtn);
        scanBarcodeBtn = (AppCompatButton) findViewById(R.id.scanBarcodeBtn);
        groceryPhotoBtn = (AppCompatButton) findViewById(R.id.groceryPhotoBtn);
        groceryPhotoShow = (AppCompatImageView) findViewById(R.id.groceryPhotoShow);

        //get the user
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //storage
        firebaseStorage = FirebaseStorage.getInstance();
        imageStorageRef = firebaseStorage.getReference();

        //category
        beautyCategory = getResources().getStringArray(R.array.personal_category);
        foodCategory = getResources().getStringArray(R.array.grocery_category);
        homeCategory = getResources().getStringArray(R.array.home_category);
        pharmacyCategory = getResources().getStringArray(R.array.pharmacy_category);
        mainCat = getResources().getStringArray(R.array.grocery_main_category);

        ArrayAdapter arrayAdapter = new ArrayAdapter(AddGroceryItemActivity.this, android.R.layout.simple_spinner_item, mainCat);
        groceryCategorySpinner.setAdapter(arrayAdapter);
        groceryCategorySpinner.setPrompt(getResources().getString(R.string.grocery_category_prompt));
        grocerySubCategorySpinner.setPrompt("Choose Category");
        groceryCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (mainCat[position].equalsIgnoreCase("Beauty & Personal Care")) {
                    ArrayAdapter adapter = new ArrayAdapter(AddGroceryItemActivity.this, android.R.layout.simple_spinner_item, beautyCategory);
                    grocerySubCategorySpinner.setAdapter(adapter);
                } else if (mainCat[position].equalsIgnoreCase("Food")) {
                    ArrayAdapter adapter = new ArrayAdapter(AddGroceryItemActivity.this, android.R.layout.simple_spinner_item, foodCategory);
                    grocerySubCategorySpinner.setAdapter(adapter);
                } else if (mainCat[position].equalsIgnoreCase("Home Essentials")) {
                    ArrayAdapter adapter = new ArrayAdapter(AddGroceryItemActivity.this, android.R.layout.simple_spinner_item, homeCategory);
                    grocerySubCategorySpinner.setAdapter(adapter);
                } else if (mainCat[position].equalsIgnoreCase("Pharmacy")) {
                    ArrayAdapter adapter = new ArrayAdapter(AddGroceryItemActivity.this, android.R.layout.simple_spinner_item, pharmacyCategory);
                    grocerySubCategorySpinner.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addGroceryItemBtn.setOnClickListener(this);
        scanBarcodeBtn.setOnClickListener(this);
        groceryPhotoBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toGrocery = new Intent(AddGroceryItemActivity.this, GroceryItemsActivity.class);
        startActivity(toGrocery);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addGroceryItemBtn:
                addGroceryitem();
                break;
            case R.id.scanBarcodeBtn:
                IntentIntegrator intentIntegrator = new IntentIntegrator(AddGroceryItemActivity.this);

                intentIntegrator.setPrompt("Use Volume Key Up to Open Flash");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(ScanBarcode.class);
                intentIntegrator.initiateScan();
                break;
            case R.id.groceryPhotoBtn:
                choosePhoto();
                break;
        }
    }

    private void choosePhoto() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2) {
            if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                groceryPhotoShow.setImageURI(imageUri);
                addGroceryitem();
            }
        } else {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (intentResult.getContents() != null) {

                if (intentResult.getFormatName().equals(IntentIntegrator.UPC_A)) {
                    groceryUPCA.setText(intentResult.getContents());
                }
            } else {
                Toast.makeText(this, "Scan failed! Try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addGroceryitem() {

        String name = groceryName.getText().toString();
        String measurement = groceryMeasurement.getText().toString();
        String price = groceryPrice.getText().toString();
        String brand = groceryBrand.getText().toString();
        String upca = groceryUPCA.getText().toString();
        String ean13 = groceryEAN13.getText().toString();
        String category = grocerySubCategorySpinner.getSelectedItem().toString();
        String mainCategory = groceryCategorySpinner.getSelectedItem().toString();


        if (name.isEmpty()) {
            groceryName.setError("This field is required!");
            groceryName.requestFocus();
            return;
        }
        if (measurement.isEmpty()) {
            groceryMeasurement.setError("This field is required!");
            groceryMeasurement.requestFocus();
            return;
        }
        if (price.isEmpty()) {
            groceryPrice.setError("This field is required!");
            groceryPrice.requestFocus();
            return;
        }
        if (brand.isEmpty()) {
            groceryBrand.setError("This field is required!");
            groceryBrand.requestFocus();
            return;
        }
        if (upca.isEmpty()) {
            groceryUPCA.setError("This field is required!");
            groceryUPCA.requestFocus();
            return;
        }
        if (ean13.isEmpty()) {
            groceryEAN13.setError("This field is required!");
            groceryEAN13.requestFocus();
            return;
        }
        if (category.isEmpty()) {
            grocerySubCategorySpinner.requestFocus();
            return;
        } else {


            DatabaseReference groceryReference = FirebaseDatabase.getInstance().getReference("Admin");

            String itemID = upca;

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading item");
            progressDialog.show();

            final String imageKey = UUID.randomUUID().toString();

            if (groceryReference.child("Grocery Items").getKey() == itemID) {

                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(AddGroceryItemActivity.this, R.style.CutShapeTheme);

                alert.setTitle("Note");
                alert.setMessage("Item " + itemID + " already exists!");
                alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            } else {
                StorageReference storageRef = imageStorageRef.child("admin/" + "groceryItems/" + mainCategory + "/" + itemID + "/"
                        + imageKey);
                storageRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        //store grocery details into realtime database
                                        GroceryItemDetail groceryItemDetail = new GroceryItemDetail(name, measurement, price, mainCategory, category, brand, upca, ean13, uri.toString());

                                        groceryReference.child("Grocery Items").child(mainCategory).child(itemID).setValue(groceryItemDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AddGroceryItemActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                                    clearField();
                                                } else {
                                                    Toast.makeText(AddGroceryItemActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        progressDialog.dismiss();
                                        Toast.makeText(AddGroceryItemActivity.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressDialog.setMessage("Progress " + (int) progressPercent + "%");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddGroceryItemActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void clearField() {
        groceryName.setText("");
        groceryMeasurement.setText("");
        groceryPrice.setText("");
        groceryBrand.setText("");
        groceryUPCA.setText("");
        groceryEAN13.setText("");
        groceryCategorySpinner.setSelection(0);

        groceryName.clearFocus();
        groceryMeasurement.clearFocus();
        groceryPrice.clearFocus();
        groceryUPCA.clearFocus();
        groceryEAN13.clearFocus();
        groceryBrand.clearFocus();
    }
}