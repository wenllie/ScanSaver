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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartNumberAdapter extends RecyclerView.Adapter<CartNumberAdapter.CartNumberViewHolder>{

    List<CartNumberClass> cartNumberClassList;
    Context context;
    MainCategorySpendingAdapter mainCategorySpendingAdapter;

    public CartNumberAdapter(List<CartNumberClass> cartNumberClassList, Context context) {
        this.cartNumberClassList = cartNumberClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartNumberAdapter.CartNumberViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_cart_number, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartNumberViewHolder holder, int position) {

        mainCategorySpendingAdapter = new MainCategorySpendingAdapter(cartNumberClassList.get(position).categoryClassList, context);
        holder.cartNumberRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.cartNumberRecycler.setAdapter(mainCategorySpendingAdapter);
        mainCategorySpendingAdapter.notifyDataSetChanged();

        holder.cartNumberText.setText(cartNumberClassList.get(position).cartNum);
    }

    @Override
    public int getItemCount() {
        try {
            return cartNumberClassList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class CartNumberViewHolder extends RecyclerView.ViewHolder{

        RecyclerView cartNumberRecycler;
        AppCompatTextView cartNumberText;

        public CartNumberViewHolder(@NonNull View itemView) {
            super(itemView);

            cartNumberText = itemView.findViewById(R.id.cartNumberText);
            cartNumberRecycler = itemView.findViewById(R.id.cartNumberRecycler);
        }
    }
}
