package com.example.scansaverapp.users.dashboard.barcodedb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.users.dashboard.ShoppingCartViewActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder> {

    Context context;
    List<GroceryItemModel> groceryList;
    int quantity = 0;
    float price = 0;
    float totalItemPrice = 0;
    float tPrice = 0;
    int qty = 0;
    AppCompatTextView totalPriceText;

    public ShoppingCartAdapter(Context context, List<GroceryItemModel> groceryList) {

        this.context = context;
        this.groceryList = groceryList;
    }

    @NonNull
    @Override
    public ShoppingCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingCartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_grocery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartViewHolder holder, @SuppressLint("RecyclerView") int position) {

        GroceryItemModel model = groceryList.get(position);

        holder.groceryItemNameTextView.setText(model.getGroceryName());
        holder.groceryMeasurementTextView.setText(model.getGroceryMeasurement());
        holder.groceryPriceTextView.setText(model.getGroceryQuantity() + " x " + model.getGroceryPrice() + " = " + model.getGroceryTotalItemPrice());
        holder.groceryQuantityTextView.setText(model.getGroceryQuantity());

        Glide.with(holder.groceryImageView.getContext())
                .load(model.getGroceryImgUrl())
                .into(holder.groceryImageView);

        //computation
        quantity = Integer.parseInt(model.getGroceryQuantity());
        qty = quantity;

        price = Float.parseFloat(model.getGroceryPrice());
        totalItemPrice = Float.parseFloat(model.getGroceryTotalItemPrice());

        Context context = holder.itemView.getContext();
        ShoppingCartViewActivity shoppingCartViewActivity = (ShoppingCartViewActivity)context;
        totalPriceText = shoppingCartViewActivity.findViewById(R.id.totalPriceText);

        //totalPriceText.setText(String.format("%.2f", grandTotal()));

        holder.ivPlusSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Thread.sleep(100);

                    qty = Integer.parseInt(model.getGroceryQuantity());

                    holder.groceryQuantityTextView.setText(String.valueOf(qty + 1));

                    tPrice = Float.parseFloat(model.getGroceryTotalItemPrice()) + Float.parseFloat(model.getGroceryPrice());
                    holder.groceryPriceTextView.setText(String.format("%.2f", tPrice));

                    Map<String, Object> update = new HashMap<>();

                    update.put("groceryQuantity", holder.groceryQuantityTextView.getText().toString());
                    update.put("groceryTotalItemPrice", holder.groceryPriceTextView.getText().toString());

                    FirebaseDatabase.getInstance().getReference("Customers")
                            .child("Shopping Cart")
                            .child(model.getCustomerId())
                            .child(model.getGroceryCategory())
                            .child(model.getGroceryUPCA())
                            .updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

        holder.ivMinusSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Thread.sleep(100);

                    qty = Integer.parseInt(model.getGroceryQuantity());

                    if (qty != 0 && !(qty <= -1)) {
                        holder.groceryQuantityTextView.setText(String.valueOf(qty - 1));

                        tPrice = Float.parseFloat(model.getGroceryTotalItemPrice()) - Float.parseFloat(model.getGroceryPrice());
                        holder.groceryPriceTextView.setText(String.format("%.2f", tPrice));

                        Map<String, Object> update = new HashMap<>();

                        update.put("groceryQuantity", holder.groceryQuantityTextView.getText().toString());
                        update.put("groceryTotalItemPrice", holder.groceryPriceTextView.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Customers")
                                .child("Shopping Cart")
                                .child(model.getCustomerId())
                                .child(model.getGroceryCategory())
                                .child(model.getGroceryUPCA())
                                .updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });
                    } else {
                        qty = 1;
                        holder.groceryQuantityTextView.setText(String.valueOf(qty));
                        Toast.makeText(context, "Quantity cannot be 0.", Toast.LENGTH_SHORT).show();

                        tPrice = Float.parseFloat(model.getGroceryTotalItemPrice()) + Float.parseFloat(model.getGroceryPrice());
                        holder.groceryPriceTextView.setText(String.format("%.2f", tPrice));

                        Map<String, Object> update = new HashMap<>();

                        update.put("groceryQuantity", holder.groceryQuantityTextView.getText().toString());
                        update.put("groceryTotalItemPrice", holder.groceryPriceTextView.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Customers")
                                .child("Shopping Cart")
                                .child(model.getCustomerId())
                                .child(model.getGroceryCategory())
                                .child(model.getGroceryUPCA())
                                .updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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

    public class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView groceryItemNameTextView, groceryMeasurementTextView, groceryPriceTextView, groceryQuantityTextView;
        AppCompatImageView groceryImageView;
        AppCompatButton ivPlusSign, ivMinusSign;

        public ShoppingCartViewHolder(@NonNull View itemView) {
            super(itemView);

            groceryItemNameTextView = itemView.findViewById(R.id.groceryItemNameTextView);
            groceryMeasurementTextView = itemView.findViewById(R.id.groceryMeasurementTextView);
            groceryPriceTextView = itemView.findViewById(R.id.groceryPriceTextView);
            groceryQuantityTextView = itemView.findViewById(R.id.groceryQuantityTextView);
            ivPlusSign = itemView.findViewById(R.id.ivPlusSign);
            ivMinusSign = itemView.findViewById(R.id.ivMinusSign);
            groceryImageView = itemView.findViewById(R.id.groceryImageView);

        }
    }
}
