package com.example.scansaverapp.users.spending.spendingdb;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.helpers_database.UserDetails;
import com.example.scansaverapp.users.dashboard.ShoppingCartViewActivity;
import com.example.scansaverapp.users.spending.SpendingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SpendingAdapter extends RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder> {

    Context applicationContext;
    List<GroceryItemModel> groceryItemModelList;

    public SpendingAdapter(Context applicationContext, List<GroceryItemModel> groceryItemModelList) {
        this.applicationContext = applicationContext;
        this.groceryItemModelList = groceryItemModelList;
    }

    @NonNull
    @Override
    public SpendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpendingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_shopping_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingViewHolder holder, int position) {

        GroceryItemModel model = groceryItemModelList.get(position);

        holder.groceryItemName.setText(model.getGroceryName());
        holder.groceryItemMeasurement.setText(model.getGroceryMeasurement());
        holder.groceryItemPrice.setText("₱" + model.getGroceryTotalItemPrice());
        holder.groceryItemQuantityXPrice.setText(model.getGroceryQuantity() + " x " + model.getGroceryPrice());

    }

    @Override
    public int getItemCount() {
        try {
            return groceryItemModelList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public class SpendingViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView groceryItemName, groceryItemMeasurement, groceryItemQuantityXPrice, groceryItemPrice;

        public SpendingViewHolder(@NonNull View itemView) {
            super(itemView);

            groceryItemName = itemView.findViewById(R.id.groceryItemName);
            groceryItemMeasurement = itemView.findViewById(R.id.groceryItemMeasurement);
            groceryItemQuantityXPrice = itemView.findViewById(R.id.groceryItemQuantityXPrice);
            groceryItemPrice = itemView.findViewById(R.id.groceryItemPrice);
        }
    }
}
