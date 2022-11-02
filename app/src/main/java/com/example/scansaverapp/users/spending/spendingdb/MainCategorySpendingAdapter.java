package com.example.scansaverapp.users.spending.spendingdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaverapp.R;
import com.example.scansaverapp.users.dashboard.barcodedb.MainCategoryClass;
import com.example.scansaverapp.users.spending.SpendingActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainCategorySpendingAdapter extends RecyclerView.Adapter<MainCategorySpendingAdapter.MCSpendingViewHolder> {

    List<MainCategorySpendingClass> mainCategorySpendingClassList;
    Context context;
    SpendingAdapter spendingAdapter;
    AppCompatTextView dateSpending, totalPriceSpending;
    Spinner chosenCategorySpinner, categorySpendingSpinner;

    int pos = 0;

    public MainCategorySpendingAdapter(List<MainCategorySpendingClass> mainCategorySpendingClassList, Context context) {
        this.mainCategorySpendingClassList = mainCategorySpendingClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public MCSpendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainCategorySpendingAdapter.MCSpendingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_spending, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MCSpendingViewHolder holder, int position) {

        pos = holder.getAdapterPosition();

        spendingAdapter = new SpendingAdapter(context, mainCategorySpendingClassList.get(position).groceryList);
        holder.mainCategoriesSpendingRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.mainCategoriesSpendingRecycler.setAdapter(spendingAdapter);
        spendingAdapter.notifyDataSetChanged();

        holder.categoryTitleSpending.setText(mainCategorySpendingClassList.get(position).title);
        holder.categoryTotalSpending.setText("₱ " + String.format("%.2f", categoryTotals(position)));

        Context context = holder.itemView.getContext();
        SpendingActivity spendingActivity = (SpendingActivity) context;

        dateSpending = spendingActivity.findViewById(R.id.dateSpending);
        totalPriceSpending = spendingActivity.findViewById(R.id.totalPriceSpending);
        chosenCategorySpinner = spendingActivity.findViewById(R.id.chosenCategorySpinner);
        categorySpendingSpinner = spendingActivity.findViewById(R.id.categorySpendingSpinner);

        dateSpending.setText(chosenCategorySpinner.getSelectedItem().toString());
        //totalPriceSpending.setText("Spending: " + String.format("%.2f", grandTotal()));

    }

    public float categoryTotals(int post) {
        float avePrice = 0;
        for (int i = 0; i < mainCategorySpendingClassList.get(post).groceryList.size(); i++) {
            avePrice += Float.parseFloat(mainCategorySpendingClassList.get(post).groceryList.get(i).getGroceryTotalItemPrice());
        }
        return avePrice;
    }

    /*public float grandTotal() {
        float grandAve = 0;
        for (int i = 0; i < 4; i++) {
            grandAve += categoryTotals(i);
        }
        return grandAve;
    }*/

    @Override
    public int getItemCount() {
        try {
            return mainCategorySpendingClassList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class MCSpendingViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView categoryTitleSpending, categoryTotalSpending;
        RecyclerView mainCategoriesSpendingRecycler;

        public MCSpendingViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTitleSpending = itemView.findViewById(R.id.categoryTitleSpending);
            categoryTotalSpending = itemView.findViewById(R.id.categoryTotalSpending);
            mainCategoriesSpendingRecycler = itemView.findViewById(R.id.mainCategoriesSpendingRecycler);
        }
    }
}
