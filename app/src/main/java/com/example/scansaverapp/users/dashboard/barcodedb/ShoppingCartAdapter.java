package com.example.scansaverapp.users.dashboard.barcodedb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.scansaverapp.R;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.users.dashboard.ShoppingCartViewActivity;
import com.example.scansaverapp.users.dashboard.SimilarItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    int selectedPOs = 0;

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


        DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Shopping Cart")
                .child(model.getCustomerId());

        //Show similar items
        holder.item_grocery_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPOs = position;

                if (selectedPOs == position) {

                    String gName = holder.groceryItemNameTextView.getText().toString();
                    float gPrice = Float.parseFloat(model.getGroceryPrice());

                    itemReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            for (DataSnapshot categorySnap : task.getResult().getChildren()) {

                                String categoryKey = categorySnap.getKey();

                                if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                    if (model.getGroceryCategory().equalsIgnoreCase(categoryKey)) {

                                        for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                            if (uidSnap.child("groceryName").getValue() == gName) {

                                                //open dialog box for the terms and conditions
                                                Dialog similarItemDialog = new Dialog(context);
                                                similarItemDialog.setContentView(R.layout.dialog_box_similar_item);

                                                RecyclerView similarItemRecycler = similarItemDialog.findViewById(R.id.similarItemRecycler);

                                                DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");
                                                shoppingCartReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                        for (DataSnapshot grocerySnap : task.getResult().getChildren()) {

                                                            String categorySnap = grocerySnap.getKey();

                                                            if (categorySnap.equalsIgnoreCase("Beauty & Personal Care")) {
                                                                groceryList.clear();

                                                                for (DataSnapshot uidSnap : grocerySnap.getChildren()) {

                                                                    String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                                                    String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                                                    float groceryPriceValues = Float.parseFloat(groceryPrice);

                                                                    boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                                                    if (isFound) {

                                                                        if (gName.equalsIgnoreCase(groceryNameValues)) {

                                                                        } else {

                                                                            if (groceryPriceValues < gPrice) {

                                                                                GroceryItemModel groceryItemModel = uidSnap.getValue(GroceryItemModel.class);
                                                                                groceryList.add(groceryItemModel);

                                                                            }

                                                                        }

                                                                    }

                                                                }
                                                            }
                                                        }

                                                        SimilarItemAdapter similarItemAdapter = new SimilarItemAdapter(context, groceryList);
                                                        similarItemRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                                        similarItemRecycler.setAdapter(similarItemAdapter);
                                                        similarItemAdapter.notifyDataSetChanged();

                                                    }
                                                });

                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(similarItemDialog.getWindow().getAttributes());
                                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                similarItemDialog.show();
                                                similarItemDialog.getWindow().setAttributes(lp);
                                                similarItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));

                                            }

                                        }
                                    }
                                } else if (categoryKey.equalsIgnoreCase("Food")) {

                                    if (model.getGroceryCategory().equalsIgnoreCase(categoryKey)) {

                                        for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                            if (uidSnap.child("groceryName").getValue() == gName) {

                                                //open dialog box for the terms and conditions
                                                Dialog similarItemDialog = new Dialog(context);
                                                similarItemDialog.setContentView(R.layout.dialog_box_similar_item);

                                                ImageView backToShoppingCartBtn = similarItemDialog.findViewById(R.id.backToShoppingCartBtn);
                                                RecyclerView similarItemRecycler = similarItemDialog.findViewById(R.id.similarItemRecycler);

                                                backToShoppingCartBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        similarItemDialog.dismiss();
                                                    }
                                                });

                                                DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");
                                                shoppingCartReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                        for (DataSnapshot grocerySnap : task.getResult().getChildren()) {

                                                            String categorySnap = grocerySnap.getKey();

                                                            if (categorySnap.equalsIgnoreCase("Food")) {
                                                                groceryList.clear();

                                                                for (DataSnapshot uidSnap : grocerySnap.getChildren()) {

                                                                    String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                                                    String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                                                    float groceryPriceValues = Float.parseFloat(groceryPrice);

                                                                    boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                                                    if (isFound) {

                                                                        if (gName.equalsIgnoreCase(groceryNameValues)) {

                                                                        } else {

                                                                            if (groceryPriceValues < gPrice) {

                                                                                GroceryItemModel groceryItemModel = uidSnap.getValue(GroceryItemModel.class);
                                                                                groceryList.add(groceryItemModel);

                                                                            }

                                                                        }

                                                                    }

                                                                }
                                                            }
                                                        }

                                                        SimilarItemAdapter similarItemAdapter = new SimilarItemAdapter(context, groceryList);
                                                        similarItemRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                                        similarItemRecycler.setAdapter(similarItemAdapter);
                                                        similarItemAdapter.notifyDataSetChanged();

                                                    }
                                                });

                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(similarItemDialog.getWindow().getAttributes());
                                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                similarItemDialog.show();
                                                similarItemDialog.getWindow().setAttributes(lp);
                                                similarItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));

                                            }

                                        }
                                    }
                                } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                    if (model.getGroceryCategory().equalsIgnoreCase(categoryKey)) {

                                        for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                            if (uidSnap.child("groceryName").getValue() == gName) {

                                                //open dialog box for the terms and conditions
                                                Dialog similarItemDialog = new Dialog(context);
                                                similarItemDialog.setContentView(R.layout.dialog_box_similar_item);

                                                ImageView backToShoppingCartBtn = similarItemDialog.findViewById(R.id.backToShoppingCartBtn);
                                                RecyclerView similarItemRecycler = similarItemDialog.findViewById(R.id.similarItemRecycler);

                                                backToShoppingCartBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        similarItemDialog.dismiss();
                                                    }
                                                });

                                                DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");
                                                shoppingCartReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                        for (DataSnapshot grocerySnap : task.getResult().getChildren()) {

                                                            String categorySnap = grocerySnap.getKey();

                                                            if (categorySnap.equalsIgnoreCase("Home Essentials")) {

                                                                groceryList.clear();

                                                                for (DataSnapshot uidSnap : grocerySnap.getChildren()) {

                                                                    String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                                                    String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                                                    float groceryPriceValues = Float.parseFloat(groceryPrice);

                                                                    boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                                                    if (isFound) {

                                                                        if (gName.equalsIgnoreCase(groceryNameValues)) {

                                                                        } else {

                                                                            if (groceryPriceValues < gPrice) {

                                                                                GroceryItemModel groceryItemModel = uidSnap.getValue(GroceryItemModel.class);
                                                                                groceryList.add(groceryItemModel);

                                                                            }

                                                                        }

                                                                    }

                                                                }
                                                            }
                                                        }

                                                        SimilarItemAdapter similarItemAdapter = new SimilarItemAdapter(context, groceryList);
                                                        similarItemRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                                        similarItemRecycler.setAdapter(similarItemAdapter);
                                                        similarItemAdapter.notifyDataSetChanged();

                                                    }
                                                });

                                                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                similarItemDialog.getWindow().setLayout(width, height);
                                                similarItemDialog.show();
                                                similarItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));

                                            }

                                        }
                                    }
                                } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                    if (model.getGroceryCategory().equalsIgnoreCase(categoryKey)) {

                                        for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                            if (uidSnap.child("groceryName").getValue() == gName) {

                                                //open dialog box for the terms and conditions
                                                Dialog similarItemDialog = new Dialog(context);
                                                similarItemDialog.setContentView(R.layout.dialog_box_similar_item);

                                                ImageView backToShoppingCartBtn = similarItemDialog.findViewById(R.id.backToShoppingCartBtn);
                                                RecyclerView similarItemRecycler = similarItemDialog.findViewById(R.id.similarItemRecycler);

                                                backToShoppingCartBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        similarItemDialog.dismiss();
                                                    }
                                                });

                                                DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");
                                                shoppingCartReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        for (DataSnapshot grocerySnap : task.getResult().getChildren()) {

                                                            String categorySnap = grocerySnap.getKey();

                                                            if (categorySnap.equalsIgnoreCase("Pharmacy")) {

                                                                groceryList.clear();

                                                                for (DataSnapshot uidSnap : grocerySnap.getChildren()) {

                                                                    String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                                                    String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                                                    float groceryPriceValues = Float.parseFloat(groceryPrice);

                                                                    boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                                                    if (isFound) {

                                                                        if (gName.equalsIgnoreCase(groceryNameValues)) {

                                                                        } else {

                                                                            if (groceryPriceValues < gPrice) {

                                                                                GroceryItemModel groceryItemModel = uidSnap.getValue(GroceryItemModel.class);
                                                                                groceryList.add(groceryItemModel);

                                                                            }

                                                                        }

                                                                    }

                                                                }
                                                            }
                                                        }

                                                        SimilarItemAdapter similarItemAdapter = new SimilarItemAdapter(context, groceryList);
                                                        similarItemRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                                        similarItemRecycler.setAdapter(similarItemAdapter);
                                                        similarItemAdapter.notifyDataSetChanged();
                                                    }
                                                });


                                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                                lp.copyFrom(similarItemDialog.getWindow().getAttributes());
                                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                similarItemDialog.show();
                                                similarItemDialog.getWindow().setAttributes(lp);
                                                similarItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));

                                            }

                                        }
                                    }
                                }


                            }

                        }
                    });

                }
            }
        });

        Context context = holder.itemView.getContext();
        ShoppingCartViewActivity shoppingCartViewActivity = (ShoppingCartViewActivity) context;

        //check the position of the card view that user clicks
        if (selectedPOs == position) {

            String gName = holder.groceryItemNameTextView.getText().toString();
            float gPrice = Float.parseFloat(model.getGroceryPrice());

            DatabaseReference similarReference = FirebaseDatabase.getInstance().getReference("Admin").child("Grocery Items");
            similarReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    for (DataSnapshot categorySnap : task.getResult().getChildren()) {

                        String categoryKey = categorySnap.getKey();

                        if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                            for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                float groceryPriceValues = Float.parseFloat(groceryPrice);

                                boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                if (isFound) {

                                    if (gName.equalsIgnoreCase(groceryNameValues)) {

                                    } else {

                                        if (gPrice > groceryPriceValues) {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#ffffff"));

                                        } else {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#e1f7d5"));

                                        }
                                    }
                                }
                            }

                        } else if (categoryKey.equalsIgnoreCase("Food")) {

                            for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                float groceryPriceValues = Float.parseFloat(groceryPrice);

                                boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                if (isFound) {

                                    if (gName.equalsIgnoreCase(groceryNameValues)) {

                                    } else {

                                        if (gPrice > groceryPriceValues) {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#ffffff"));

                                        } else {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#e1f7d5"));

                                        }
                                    }
                                }
                            }

                        } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                            for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                float groceryPriceValues = Float.parseFloat(groceryPrice);

                                boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                if (isFound) {

                                    if (gName.equalsIgnoreCase(groceryNameValues)) {

                                    } else {

                                        if (gPrice > groceryPriceValues) {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#ffffff"));

                                        } else {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#e1f7d5"));

                                        }
                                    }
                                }
                            }

                        } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                            for (DataSnapshot uidSnap : categorySnap.getChildren()) {

                                String groceryNameValues = uidSnap.child("groceryName").getValue().toString();
                                String groceryPrice = uidSnap.child("groceryPrice").getValue().toString();
                                float groceryPriceValues = Float.parseFloat(groceryPrice);

                                boolean isFound = gName.indexOf(groceryNameValues) != 1;

                                if (isFound) {

                                    if (gName.equalsIgnoreCase(groceryNameValues)) {

                                    } else {

                                        if (gPrice > groceryPriceValues) {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#ffffff"));

                                        } else {

                                            holder.item_grocery_main_container.setBackgroundColor(Color.parseColor("#e1f7d5"));

                                        }
                                    }
                                }
                            }

                        }


                    }

                }
            });

        }

        //computation
        quantity = Integer.parseInt(model.getGroceryQuantity());
        qty = quantity;

        price = Float.parseFloat(model.getGroceryPrice());
        totalItemPrice = Float.parseFloat(model.getGroceryTotalItemPrice());

        /*Context context = holder.itemView.getContext();
        ShoppingCartViewActivity shoppingCartViewActivity = (ShoppingCartViewActivity) context;*/
        //totalPriceText = shoppingCartViewActivity.findViewById(R.id.totalPriceText);

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
        LinearLayoutCompat item_grocery_container, item_grocery_main_container;


        public ShoppingCartViewHolder(@NonNull View itemView) {
            super(itemView);

            groceryItemNameTextView = itemView.findViewById(R.id.groceryItemNameTextView);
            groceryMeasurementTextView = itemView.findViewById(R.id.groceryMeasurementTextView);
            groceryPriceTextView = itemView.findViewById(R.id.groceryPriceTextView);
            groceryQuantityTextView = itemView.findViewById(R.id.groceryQuantityTextView);
            ivPlusSign = itemView.findViewById(R.id.ivPlusSign);
            ivMinusSign = itemView.findViewById(R.id.ivMinusSign);
            groceryImageView = itemView.findViewById(R.id.groceryImageView);
            item_grocery_container = itemView.findViewById(R.id.item_grocery_container);
            item_grocery_main_container = itemView.findViewById(R.id.item_grocery_main_container);

        }
    }
}
