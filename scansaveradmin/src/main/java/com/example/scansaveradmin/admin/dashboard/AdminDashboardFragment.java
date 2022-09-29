package com.example.scansaveradmin.admin.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.complaints.ComplaintsActivity;
import com.example.scansaveradmin.admin.customers.CustomersActivity;
import com.example.scansaveradmin.admin.items.GroceryItemsActivity;
import com.example.scansaveradmin.admin.requests.RequestsActivity;
import com.example.scansaveradmin.databinding.FragmentAdminDashboardBinding;
import com.google.android.material.card.MaterialCardView;

public class AdminDashboardFragment extends Fragment implements View.OnClickListener{

    MaterialCardView manageItems, manageRequests, manageComplaints, manageUsers;

    private FragmentAdminDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        manageRequests = (MaterialCardView) root.findViewById(R.id.manageRequests);
        manageComplaints = (MaterialCardView) root.findViewById(R.id.manageComplaints);
        manageItems = (MaterialCardView) root.findViewById(R.id.manageItems);
        manageUsers = (MaterialCardView) root.findViewById(R.id.manageUsers);

        manageRequests.setOnClickListener(this);
        manageComplaints.setOnClickListener(this);
        manageItems.setOnClickListener(this);
        manageUsers.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manageUsers:
                getActivity().startActivity(new Intent(getActivity(), CustomersActivity.class));
                break;
            case R.id.manageItems:
                getActivity().startActivity(new Intent(getActivity(), GroceryItemsActivity.class));

                break;
            case R.id.manageComplaints:
                getActivity().startActivity(new Intent(getActivity(), ComplaintsActivity.class));

                break;
            case R.id.manageRequests:
                getActivity().startActivity(new Intent(getActivity(), RequestsActivity.class));

                break;
        }
    }
}