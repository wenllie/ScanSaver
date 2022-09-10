package com.example.scansaverapp.users.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.google.android.material.navigation.NavigationView;

public class ScanBarcodeActivity extends AppCompatActivity implements View.OnClickListener{

    AppCompatImageView backBtn;
    AppCompatImageView scannerOpenCameraBtn;
    AppCompatButton scannerBarcodeBtn, scannerViewCartBtn, scannerToDashboardBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        scannerBarcodeBtn = (AppCompatButton) findViewById(R.id.scannerBarcodeBtn);
        scannerViewCartBtn = (AppCompatButton) findViewById(R.id.scannerViewCartBtn);
        scannerOpenCameraBtn = (AppCompatImageView) findViewById(R.id.scannerOpenCameraBtn);

        scannerBarcodeBtn.setOnClickListener(this);
        scannerViewCartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scanBarcodeBtn:

                break;
            case R.id.scannerViewCartBtn:
                Intent cart = new Intent(ScanBarcodeActivity.this, ShoppingCartViewActivity.class);
                startActivity(cart);
                break;
            case R.id.scannerOpenCameraBtn:
                break;
        }
    }

}