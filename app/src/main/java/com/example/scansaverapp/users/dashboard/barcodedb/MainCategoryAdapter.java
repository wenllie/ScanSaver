package com.example.scansaverapp.users.dashboard.barcodedb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaverapp.R;
import com.example.scansaverapp.users.dashboard.ShoppingCartViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.MainCategoryViewHolder> {

    List<MainCategoryClass> mainCategoryClassList;
    Context context;
    AppCompatTextView totalPriceText;
    ShoppingCartAdapter shoppingCartAdapter;

    int pos = 0;

    public MainCategoryAdapter(List<MainCategoryClass> mainCategoryClassList, Context context) {
        this.mainCategoryClassList = mainCategoryClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_grocery_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainCategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        pos = holder.getAdapterPosition();

        shoppingCartAdapter = new ShoppingCartAdapter(context, mainCategoryClassList.get(position).groceryList);
        holder.mainCategoriesRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.mainCategoriesRecycler.setAdapter(shoppingCartAdapter);
        shoppingCartAdapter.notifyDataSetChanged();

        holder.categoryTitle.setText(mainCategoryClassList.get(position).title);

        //holder.grocery_items_card_view.setCardBackgroundColor(holder.itemView.getResources().getColor(getRedColor(), null));

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Customers").child("Budget")
                .child(FirebaseAuth.getInstance().getUid());

        foodRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String catTotals = (String) snap.getValue();
                    String key = snap.getKey();

                    if (holder.categoryTitle.getText().toString().equalsIgnoreCase("Beauty & Personal Care")) {
                        String title = "beautyAndPersonalCare";
                        if (title.equalsIgnoreCase(key)) {
                            String stringTotals = holder.categoryTotal.getText().toString().replaceAll("[^.0-9]", "");
                            float totals = Float.parseFloat(stringTotals);
                            if (totals > Float.parseFloat(catTotals)) {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.red));
                                Toast.makeText(context, "Your cart Beauty & Personal Care exceeded your budget!", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.blue_main));
                            }
                        }
                    } else if (holder.categoryTitle.getText().toString().equalsIgnoreCase("Food")) {
                        String title = "food";
                        if (title.equalsIgnoreCase(key)) {
                            String stringTotals = holder.categoryTotal.getText().toString().replaceAll("[^.0-9]", "");
                            float totals = Float.parseFloat(stringTotals);
                            if (totals > Float.parseFloat(catTotals)) {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.red));
                                Toast.makeText(context, "Your Food cart exceeded your budget!", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.blue_main));
                            }
                        }
                    } else if (holder.categoryTitle.getText().toString().equalsIgnoreCase("Home Essentials")) {
                        String title = "homeEssentials";
                        if (title.equalsIgnoreCase(key)) {
                            String stringTotals = holder.categoryTotal.getText().toString().replaceAll("[^.0-9]", "");
                            float totals = Float.parseFloat(stringTotals);
                            if (totals > Float.parseFloat(catTotals)) {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.red));
                                Toast.makeText(context, "Your Home Essentials cart exceeded your budget!", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.blue_main));
                            }
                        }
                    } else if (holder.categoryTitle.getText().toString().equalsIgnoreCase("Pharmacy")) {
                        String title = "pharmacy";
                        if (title.equalsIgnoreCase(key)) {
                            String stringTotals = holder.categoryTotal.getText().toString().replaceAll("[^.0-9]", "");
                            float totals = Float.parseFloat(stringTotals);
                            if (totals > Float.parseFloat(catTotals)) {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.red));
                                Toast.makeText(context, "Your Pharmacy cart exceeded your budget!", Toast.LENGTH_SHORT).show();

                            } else {
                                holder.categoryTotal.setTextColor(context.getResources().getColor(R.color.blue_main));
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.categoryTotal.setText("₱ " + String.format("%.2f", categoryTotals(position)));

        /*Context context = holder.itemView.getContext();
        ShoppingCartViewActivity shoppingCartViewActivity = (ShoppingCartViewActivity) context;
        totalPriceText = shoppingCartViewActivity.findViewById(R.id.totalPriceText);

        totalPriceText.setText(String.format("%.2f", grandTotal()));*/

    }

    public float grandTotal() {
        float grandAve = 0;
        float aveTotal = 0;

        for (int i = 0; i < 4; i++) {
            grandAve += categoryTotals(i);
            aveTotal = grandAve /4;

        }
        return aveTotal;
    }

    public float categoryTotals(int post) {
        post = pos;
        float avePrice = 0;
        for (int i = 0; i < mainCategoryClassList.get(pos).groceryList.size(); i++) {
            avePrice += Float.parseFloat(mainCategoryClassList.get(post).groceryList.get(i).getGroceryTotalItemPrice());
        }
        return avePrice;
    }

    @Override
    public int getItemCount() {
        try {
            return mainCategoryClassList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class MainCategoryViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView categoryTitle, categoryTotal;
        RecyclerView mainCategoriesRecycler;

        public MainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            categoryTotal = itemView.findViewById(R.id.categoryTotal);
            mainCategoriesRecycler = itemView.findViewById(R.id.mainCategoriesRecycler);

        }
    }
}
