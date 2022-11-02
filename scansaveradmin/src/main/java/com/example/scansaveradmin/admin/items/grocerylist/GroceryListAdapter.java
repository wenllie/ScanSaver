package com.example.scansaveradmin.admin.items.grocerylist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scansaveradmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GrocerylistViewHolder> {

    Context context;
    List<GroceryListModel> groceryList;

    public GroceryListAdapter(Context context, List<GroceryListModel> groceryList) {
        this.context = context;
        this.groceryList = groceryList;
    }

    public class GrocerylistViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView groceryName, groceryMeasurement;
        AppCompatButton updateGroceryBtn, deleteGroceryBtn;
        AppCompatImageView groceryItemPhoto;

        public GrocerylistViewHolder(@NonNull View itemView) {
            super(itemView);

            groceryName = itemView.findViewById(R.id.groceryItemNameTextView);
            groceryMeasurement = itemView.findViewById(R.id.groceryItemMeasurementTextView);
            updateGroceryBtn = itemView.findViewById(R.id.updateGroceryItemBtn);
            deleteGroceryBtn = itemView.findViewById(R.id.deleteDeleteBtn);
            groceryItemPhoto = itemView.findViewById(R.id.groceryItemPhoto);
        }
    }

    @NonNull
    @Override
    public GrocerylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_grocery_list, parent, false);
        return new GrocerylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrocerylistViewHolder holder, @SuppressLint("RecyclerView") int position) {

        GroceryListModel model = groceryList.get(position);

        holder.groceryName.setText(model.getGroceryName());
        holder.groceryMeasurement.setText(model.getGroceryMeasurement());

        Glide.with(holder.groceryItemPhoto.getContext())
                        .load(model.getGroceryImgUrl())
                                .into(holder.groceryItemPhoto);

        holder.updateGroceryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus updateDialog = DialogPlus.newDialog(holder.itemView.getContext())
                        .setContentHolder(new ViewHolder(R.layout.pop_up_update_grocery_item))
                        .setExpanded(true, 1500)
                        .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                        .create();
                View v = updateDialog.getHolderView();

                TextInputEditText updateGroceryName = v.findViewById(R.id.updateGroceryName);
                TextInputEditText updateGroceryMeasurement = v.findViewById(R.id.updateGroceryMeasurement);
                TextInputEditText updateGroceryPrice = v.findViewById(R.id.updateGroceryPrice);
                AppCompatTextView updateGroceryCategory = v.findViewById(R.id.updateGroceryCategory);
                AppCompatTextView updateGrocerySubCategory = v.findViewById(R.id.updateSubGroceryCategory);
                TextInputEditText updateGroceryBrand = v.findViewById(R.id.updateGroceryBrand);
                TextInputEditText updateGroceryUPCA = v.findViewById(R.id.updateGroceryUPCA);
                TextInputEditText updateGroceryEAN13 = v.findViewById(R.id.updateGroceryEAN13);
                AppCompatImageView groceryPhotoShow = v.findViewById(R.id.groceryPhotoShow);
                AppCompatButton updateGroceryPhotoBtn = v.findViewById(R.id.updateGroceryPhotoBtn);
                AppCompatButton updateGroceryItemBtn = v.findViewById(R.id.updateGroceryItemBtn);

                updateGroceryName.setText(model.getGroceryName());
                updateGroceryMeasurement.setText(model.getGroceryMeasurement());
                updateGroceryPrice.setText(model.getGroceryPrice());
                updateGroceryBrand.setText(model.getGroceryBrand());
                updateGroceryUPCA.setText(model.getGroceryUPCA());
                updateGroceryEAN13.setText(model.getGroceryEAN13());
                updateGroceryCategory.setText(model.getGroceryCategory());
                updateGrocerySubCategory.setText(model.getGrocerySubCategory());

                Glide.with(groceryPhotoShow.getContext())
                        .load(model.getGroceryImgUrl())
                        .into(groceryPhotoShow);

                updateDialog.show();

                updateGroceryItemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> updateGrocery = new HashMap<>();
                        String itemID = groceryList.get(position).groceryUPCA;

                        updateGrocery.put("groceryName", updateGroceryName.getText().toString());
                        updateGrocery.put("groceryMeasurement", updateGroceryMeasurement.getText().toString());
                        updateGrocery.put("groceryPrice", updateGroceryPrice.getText().toString());
                        updateGrocery.put("groceryCategory", updateGroceryCategory.getText().toString());
                        updateGrocery.put("grocerySubCategory", updateGrocerySubCategory.getText().toString());
                        updateGrocery.put("groceryBrand", updateGroceryBrand.getText().toString());
                        updateGrocery.put("groceryUPCA", updateGroceryUPCA.getText().toString());
                        updateGrocery.put("groceryEAN13", updateGroceryEAN13.getText().toString());

                        //alert dialog for updating the details of grocery item
                        MaterialAlertDialogBuilder updateDetailBuilder = new MaterialAlertDialogBuilder(holder.groceryName.getContext(), R.style.CutShapeTheme);
                        updateDetailBuilder.setTitle("Update Grocery Item Details");
                        updateDetailBuilder.setMessage("Are you sure you want to update the details of " + groceryList.get(position).getGroceryName().toString() +
                                "? \n\nPlease note that once you update a detail, it cannot be reverted back to the old value.");

                        updateDetailBuilder
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        FirebaseDatabase.getInstance().getReference("Admin")
                                                .child("Grocery Items").child(itemID).updateChildren(updateGrocery)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(holder.groceryName.getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                                                        updateDialog.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(holder.groceryName.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        holder.deleteGroceryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder deleteItemBuilder = new MaterialAlertDialogBuilder(holder.groceryName.getContext(), R.style.CutShapeTheme);
                deleteItemBuilder.setTitle("Delete Item");
                deleteItemBuilder.setMessage("Are you sure you want to delete " + groceryList.get(position).getGroceryName().toString() + "? \n\nPlease note that once you delete an item, it cannot be undone.");

                deleteItemBuilder
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String itemID = groceryList.get(position).groceryUPCA;

                                FirebaseDatabase.getInstance().getReference("Admin")
                                        .child("Grocery Items").child(itemID).removeValue();

                                Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                deleteItemBuilder.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        try {
            return groceryList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public void filteredGroceryList(List<GroceryListModel> filteredList){

        groceryList =   filteredList;
        notifyDataSetChanged();

    }
}
