package com.example.scansaverapp.users.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.users.dashboard.barcodedb.ShoppingCartAdapter;

import java.util.List;

public class SimilarItemAdapter extends RecyclerView.Adapter<SimilarItemAdapter.SimilarItemViewHolder>{

    Context context;
    List<GroceryItemModel> groceryList;

    public SimilarItemAdapter(Context context, List<GroceryItemModel> groceryList) {
        this.context = context;
        this.groceryList = groceryList;
    }

    @NonNull
    @Override
    public SimilarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimilarItemAdapter.SimilarItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_similar_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarItemViewHolder holder, int position) {

        GroceryItemModel model = groceryList.get(position);

        holder.similarGroceryName.setText(model.getGroceryName());
        holder.similarGroceryMeasurement.setText(model.getGroceryMeasurement());
        holder.similarGroceryPrice.setText(model.getGroceryPrice());

        Glide.with(holder.similarGroceryImageView.getContext())
                .load(model.getGroceryImgUrl())
                .into(holder.similarGroceryImageView);


    }

    @Override
    public int getItemCount() {
        try {
            return groceryList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public class SimilarItemViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView similarGroceryImageView;
        AppCompatTextView similarGroceryName, similarGroceryMeasurement, similarGroceryPrice;

        public SimilarItemViewHolder(@NonNull View itemView) {
            super(itemView);

            similarGroceryImageView = itemView.findViewById(R.id.similarGroceryImageView);
            similarGroceryName = itemView.findViewById(R.id.similarGroceryName);
            similarGroceryMeasurement = itemView.findViewById(R.id.similarGroceryMeasurement);
            similarGroceryPrice = itemView.findViewById(R.id.similarGroceryPrice);

        }
    }
}
