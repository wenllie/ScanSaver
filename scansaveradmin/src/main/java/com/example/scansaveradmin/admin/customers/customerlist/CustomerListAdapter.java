package com.example.scansaveradmin.admin.customers.customerlist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.items.grocerylist.GroceryListModel;
import com.github.hariprasanths.floatingtoast.FloatingToast;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder> {

    Context context;
    List<CustomerListModel> customerList;

    public CustomerListAdapter(Context applicationContext, List<CustomerListModel> customerList) {

        this.context = applicationContext;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_customer_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CustomerListModel model = customerList.get(position);


        holder.fullName.setText(model.getFullName());
        holder.email.setText(model.getEmail());
        holder.phoneNumber.setText(model.getPhoneNumber());

        //update customer detail
        holder.customerUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog plus
                final DialogPlus updateDialog = DialogPlus.newDialog(holder.itemView.getContext())
                        .setContentHolder(new ViewHolder(R.layout.pop_up_customer_details))
                        .setExpanded(true, 1200)
                        .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                        .create();
                View v = updateDialog.getHolderView();

                //setting the customer details in the text fields
                TextInputEditText updateCustomerName = v.findViewById(R.id.updateCustomerName);
                TextInputEditText updateCustomerBirthday = v.findViewById(R.id.updateCustomerBirthday);
                TextInputEditText updateCustomerAge = v.findViewById(R.id.updateCustomerAge);
                AppCompatSpinner updateCustomerGenderSpinner = v.findViewById(R.id.updateCustomerGenderSpinner);
                TextInputEditText updateCustomerEmail = v.findViewById(R.id.updateCustomerEmail);
                TextInputEditText updateCustomerPhoneNumber = v.findViewById(R.id.updateCustomerPhoneNumber);
                AppCompatButton updateCustomerInfoBtn = v.findViewById(R.id.updateCustomerInfoBtn);

                updateCustomerName.setText(customerList.get(position).getFullName());
                updateCustomerBirthday.setText(customerList.get(position).getBirthday());
                updateCustomerAge.setText(customerList.get(position).getAge());
                updateCustomerGenderSpinner.equals(customerList.get(position).getGender());
                updateCustomerEmail.setText(customerList.get(position).getEmail());
                updateCustomerPhoneNumber.setText(customerList.get(position).getPhoneNumber());

                updateDialog.show();

                //function for updating customer details
                updateCustomerInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String customerId = customerList.get(position).getCustomerId();
                        Map<String, Object> updateCustomer = new HashMap<>();

                        updateCustomer.put("fullName", updateCustomerName.getText().toString());
                        updateCustomer.put("birthday", updateCustomerBirthday.getText().toString());
                        updateCustomer.put("age", updateCustomerAge.getText().toString());
                        updateCustomer.put("gender", updateCustomerGenderSpinner.getSelectedItem().toString());
                        updateCustomer.put("email", updateCustomerEmail.getText().toString());
                        updateCustomer.put("phoneNumber", updateCustomerPhoneNumber.getText().toString());

                        //alter dialog for updating the details of the customer
                        MaterialAlertDialogBuilder updateDetailBuilder = new MaterialAlertDialogBuilder(holder.fullName.getContext(), R.style.CutShapeTheme);
                        updateDetailBuilder.setTitle("Update Customer Details");
                        updateDetailBuilder.setMessage("Are you sure you want to update the details of " + customerList.get(position).getFullName().toString() +
                                "? \n\nPlease note that once you update a detail, it cannot be reverted back to the old value.");

                        updateDetailBuilder
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        FirebaseDatabase.getInstance().getReference("Customers")
                                                .child("Personal Information")
                                                .child(customerId)
                                                .updateChildren(updateCustomer)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(holder.fullName.getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
//                                                        FloatingToast.makeToast(holder.customerUpdateBtn, "Updated Successfully!", FloatingToast.LENGTH_LONG)
//                                                                .setGravity(FloatingToast.GRAVITY_MID_BOTTOM)
//                                                                .setFadeOutDuration(FloatingToast.FADE_DURATION_LONG)
//                                                                .setTextSizeInDp(16)
//                                                                .setBackgroundBlur(true)
//                                                                .setFloatDistance(FloatingToast.DISTANCE_SHORT)
//                                                                .setTextColor(Color.parseColor("#ffffff"))
//                                                                .setShadowLayer(5, 1, 1, Color.parseColor("#000000"))
//                                                                .show();
                                                        updateDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(holder.fullName.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        updateDetailBuilder.show();

                    }
                });

            }
        });

        //delete customer detail
        holder.customerDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder deleteDetailBuilder = new MaterialAlertDialogBuilder(holder.fullName.getContext(), R.style.CutShapeTheme);
                deleteDetailBuilder.setTitle("Delete Customer");
                deleteDetailBuilder.setMessage("Are you sure you want to delete " + customerList.get(position).getFullName().toString() +
                        "? \n\nPlease note that once you press delete, it cannot be undone.");

                deleteDetailBuilder
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String customerId = customerList.get(position).customerId;

                                FirebaseDatabase.getInstance().getReference("Customers").child("Personal Information").child(customerId).removeValue();

                                Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show();

//                                FloatingToast.makeToast(holder.customerDeleteBtn, R.string.delete_success, FloatingToast.LENGTH_LONG)
//                                        .setGravity(FloatingToast.GRAVITY_MID_TOP)
//                                        .setFadeOutDuration(FloatingToast.FADE_DURATION_LONG)
//                                        .setTextSizeInDp(12)
//                                        .setBackgroundBlur(true)
//                                        .setFloatDistance(FloatingToast.DISTANCE_SHORT)
//                                        .show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                deleteDetailBuilder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return customerList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public class CustomerListViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView fullName, email, phoneNumber;
        AppCompatButton customerUpdateBtn, customerDeleteBtn;

        public CustomerListViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = (AppCompatTextView) itemView.findViewById(R.id.customerNameTextView);
            email = (AppCompatTextView) itemView.findViewById(R.id.customerEmailTextView);
            phoneNumber = (AppCompatTextView) itemView.findViewById(R.id.customerPhoneNumberTextView);
            customerUpdateBtn = itemView.findViewById(R.id.customerUpdateBtn);
            customerDeleteBtn = itemView.findViewById(R.id.customerDeleteBtn);
        }
    }
}
