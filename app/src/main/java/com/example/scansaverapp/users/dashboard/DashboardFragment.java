package com.example.scansaverapp.users.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.databinding.FragmentDashboardBinding;
import com.example.scansaverapp.users.spending.SpendingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardFragment extends Fragment{

    private FragmentDashboardBinding binding;

    CardView scanBarcodeBtn, spendingPDFBtn;
    TextView morningOrNight, userName, currentDate;

    private Calendar calendar;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scanBarcodeBtn = root.findViewById(R.id.scanBarcodeBtn);
        spendingPDFBtn = root.findViewById(R.id.spendingPDFBtn);
        morningOrNight = root.findViewById(R.id.morningOrNight);
        userName = root.findViewById(R.id.userName);
        currentDate = root.findViewById(R.id.currentDate);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        String month = monthName[calendar.get(Calendar.MONTH)];

        currentDate.setText(String.valueOf(day) + " " + month + "\n" + String.valueOf(year));

        int timeOfTheDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfTheDay >= 0 && timeOfTheDay < 12) {
            morningOrNight.setText("Good morning!");
        } else if (timeOfTheDay >= 12 && timeOfTheDay < 16) {
            morningOrNight.setText("Good afternoon!");
        } else if (timeOfTheDay >= 16 && timeOfTheDay < 21) {
            morningOrNight.setText("Good evening!");
        } else if (timeOfTheDay >= 21 && timeOfTheDay < 24) {
            morningOrNight.setText("Good night!");
        }

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Personal Information").child(FirebaseAuth.getInstance().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.child("fullName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        scanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater =(LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.activity_scan_barcode, null);
                Intent viewScannerBarcodeActivity = new Intent();
                viewScannerBarcodeActivity.setClass(view.getContext(), ScanBarcodeActivity.class);
                view.getContext().startActivity(viewScannerBarcodeActivity);
                getActivity().finish();
            }
        });

        spendingPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent spending = new Intent(getActivity(), SpendingActivity.class);
                getActivity().startActivity(spending);
                getActivity().finish();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}