package com.example.scansaverapp.users.spending.spendingdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaverapp.R;

import java.util.List;
import java.util.zip.Inflater;

public class MonthNameAdapter extends RecyclerView.Adapter<MonthNameAdapter.MonthNameViewHolder> {

    List<MonthNameClass> monthNameList;
    Context context;
    CartNumberAdapter cartNumberAdapter;
    AppCompatTextView dateSpending, totalPriceSpending;

    public MonthNameAdapter(List<MonthNameClass> monthNameList, Context context) {
        this.monthNameList = monthNameList;
        this.context = context;
    }

    @NonNull
    @Override
    public MonthNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MonthNameAdapter.MonthNameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_month_spending, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MonthNameViewHolder holder, int position) {

        cartNumberAdapter = new CartNumberAdapter(monthNameList.get(position).monthNameList, context);
        holder.monthNameRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.monthNameRecycler.setAdapter(cartNumberAdapter);
        cartNumberAdapter.notifyDataSetChanged();

        holder.monthNameText.setText(monthNameList.get(position).monthName);


    }

    @Override
    public int getItemCount() {
        try {
            return monthNameList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class MonthNameViewHolder  extends RecyclerView.ViewHolder{

        RecyclerView monthNameRecycler;
        AppCompatTextView monthNameText;

        public MonthNameViewHolder(@NonNull View itemView) {
            super(itemView);

            monthNameRecycler = itemView.findViewById(R.id.monthNameRecycler);
            monthNameText = itemView.findViewById(R.id.monthNameText);
        }
    }
}
