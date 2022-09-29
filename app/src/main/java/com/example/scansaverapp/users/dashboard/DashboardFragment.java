package com.example.scansaverapp.users.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.scansaverapp.R;
import com.example.scansaverapp.databinding.FragmentDashboardBinding;
import com.example.scansaverapp.users.spending.SpendingActivity;

public class DashboardFragment extends Fragment implements View.OnClickListener{

    private FragmentDashboardBinding binding;

    public static final int REQUEST_CODE = 11;
    public static final int RESULT_CODE = 12;
    public static final String EXTRA_KEY_TEST = "testKey";

    LinearLayoutCompat dashboardFrag;

    AppCompatButton scanBarcodeBtn, shoppingCartBtn, spendingPDFBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        scanBarcodeBtn = (AppCompatButton) root.findViewById(R.id.scanBarcodeBtn);
        shoppingCartBtn = (AppCompatButton) root.findViewById(R.id.shoppingCartBtn);
        spendingPDFBtn = (AppCompatButton) root.findViewById(R.id.spendingPDFBtn);
        dashboardFrag = (LinearLayoutCompat) root.findViewById(R.id.dashboardFrag);

        scanBarcodeBtn.setOnClickListener(this);
        shoppingCartBtn.setOnClickListener(this);
        spendingPDFBtn.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scanBarcodeBtn:
//                Intent scan = new Intent(getActivity(), ScanBarcodeActivity.class);
//                getActivity().startActivity(scan);

                LayoutInflater inflater =(LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.activity_scan_barcode, null);
                Intent viewScannerBarcodeActivity = new Intent();
                viewScannerBarcodeActivity.setClass(view.getContext(), ScanBarcodeActivity.class);
                view.getContext().startActivity(viewScannerBarcodeActivity);

                break;
            case R.id.shoppingCartBtn:
                getActivity().startActivity(new Intent(getActivity(), ShoppingCartViewActivity.class));
                break;
            case R.id.spendingPDFBtn:
                Intent spending = new Intent(getActivity(), SpendingActivity.class);
                getActivity().startActivity(spending);
                break;

        }
    }

}