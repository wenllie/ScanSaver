package com.example.scansaverapp.users.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.users.dashboard.barcodedb.MainCategoryAdapter;
import com.example.scansaverapp.users.dashboard.barcodedb.MainCategoryClass;
import com.example.scansaverapp.users.dashboard.barcodedb.ShoppingCartAdapter;
import com.example.scansaverapp.users.helpcenter.HelpCenterActivity;
import com.example.scansaverapp.users.spending.SpendingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShoppingCartViewActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frCartToDashboard;
    AppCompatButton saveShoppingCartBtn;
    AppCompatTextView budgetDateTextView;
    RelativeLayout shoppingCartViewActivityLayout;
    AppCompatTextView beautyAndPersonalCareBudget, foodBudget, homeEssentialsBudget, pharmacyBudget;

    //Shopping cart List
    RecyclerView main_recycler;
    List<GroceryItemModel> groceryList;
    List<GroceryItemModel> foodList;
    List<GroceryItemModel> beautyList;
    List<GroceryItemModel> homeList;
    List<GroceryItemModel> pharmacyList;
    List<MainCategoryClass> categoryList;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String currentDate;
    private int currentYear;
    private String currentMonth;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    public static String userID;
    private FirebaseUser user;
    private DatabaseReference fromShoppingCartRef = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart");
    private DatabaseReference toInventoriesRef = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories");
    private DatabaseReference budgetReference = FirebaseDatabase.getInstance().getReference("Customers").child("Budget");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_view);

        frCartToDashboard = (ImageView) findViewById(R.id.frCartToDashboard);
        saveShoppingCartBtn = (AppCompatButton) findViewById(R.id.saveShoppingCartBtn);
        main_recycler = (RecyclerView) findViewById(R.id.main_recycler);
        shoppingCartViewActivityLayout = (RelativeLayout) findViewById(R.id.shoppingCartViewActivityLayout);

        //budget
        beautyAndPersonalCareBudget = (AppCompatTextView) findViewById(R.id.beautyAndPersonalCareBudget);
        foodBudget = (AppCompatTextView) findViewById(R.id.foodBudget);
        homeEssentialsBudget = (AppCompatTextView) findViewById(R.id.homeEssentialsBudget);
        pharmacyBudget = (AppCompatTextView) findViewById(R.id.pharmacyBudget);
        budgetDateTextView = (AppCompatTextView) findViewById(R.id.budgetDateTextView);

        calendar = Calendar.getInstance();
        int budgetYear = calendar.get(Calendar.YEAR);
        String budgetMonth = monthName[calendar.get(Calendar.MONTH)];
        budgetDateTextView.setText(budgetMonth + " " + String.valueOf(budgetYear));

        //get the user ID of the user
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //set customer budget
        setBudget();

        //show cart
        showCart();

        //handle swipe left of grocery item
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(main_recycler);


        frCartToDashboard.setOnClickListener(this);
        saveShoppingCartBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toScan = new Intent(ShoppingCartViewActivity.this, ScanBarcodeActivity.class);
        toScan.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toScan);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frCartToDashboard:
                onBackPressed();
                break;
            case R.id.saveShoppingCartBtn:
                savedShoppingCart(fromShoppingCartRef, toInventoriesRef, FirebaseAuth.getInstance().getUid());
                break;
        }
    }

    private void showCart() {

        //show customer details in the recycler view
        groceryList = new ArrayList<>();
        foodList = new ArrayList<>();
        beautyList = new ArrayList<>();
        homeList = new ArrayList<>();
        pharmacyList = new ArrayList<>();
        categoryList = new ArrayList<>();

        DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart");
        shoppingCartReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();

                for (DataSnapshot grocerySnap : snapshot.getChildren()) {

                    String categorySnap = grocerySnap.getKey();

                    if (categorySnap.equalsIgnoreCase("Food")) {
                        foodList.clear();

                        for (DataSnapshot snap : grocerySnap.getChildren()) {
                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                            foodList.add(groceryItemModel);
                        }
                        categoryList.add(new MainCategoryClass(categorySnap, foodList));
                    }

                    if (categorySnap.equalsIgnoreCase("Beauty & Personal Care")) {
                        beautyList.clear();

                        for (DataSnapshot snap : grocerySnap.getChildren()) {

                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                            beautyList.add(groceryItemModel);
                        }
                        categoryList.add(new MainCategoryClass(categorySnap, beautyList));

                    }

                    if (categorySnap.equalsIgnoreCase("Home Essentials")) {
                        homeList.clear();

                        for (DataSnapshot snap : grocerySnap.getChildren()) {

                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                            homeList.add(groceryItemModel);
                        }
                        categoryList.add(new MainCategoryClass(categorySnap, homeList));

                    }

                    if (categorySnap.equalsIgnoreCase("Pharmacy")) {
                        pharmacyList.clear();

                        for (DataSnapshot snap : grocerySnap.getChildren()) {

                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                            pharmacyList.add(groceryItemModel);
                        }
                        categoryList.add(new MainCategoryClass(categorySnap, pharmacyList));

                    }

                }

                MainCategoryAdapter mainCategoryAdapter = new MainCategoryAdapter(categoryList, ShoppingCartViewActivity.this);
                main_recycler.setLayoutManager(new LinearLayoutManager(ShoppingCartViewActivity.this, LinearLayoutManager.VERTICAL, false));
                main_recycler.setItemAnimator(null);
                main_recycler.setAdapter(mainCategoryAdapter);
                mainCategoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShoppingCartViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void setBudget() {
        String budgetMonth = monthName[calendar.get(Calendar.MONTH)];

        budgetReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.getResult().child(userID).exists()) {

                    DataSnapshot userSnap = task.getResult().child(userID);

                    for (DataSnapshot snapshot : userSnap.getChildren()) {
//                            Toast.makeText(ShoppingCartViewActivity.this, snapshot.getKey() + snapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                        String key = snapshot.getKey();
                        if (key.equalsIgnoreCase("beautyAndPersonalCare")) {
                            beautyAndPersonalCareBudget.setText(snapshot.getValue().toString());
                        } else if (key.equalsIgnoreCase("food")) {
                            foodBudget.setText(snapshot.getValue().toString());
                        } else if (key.equalsIgnoreCase("homeEssentials")) {
                            homeEssentialsBudget.setText(snapshot.getValue().toString());
                        } else if (key.equalsIgnoreCase("pharmacy")) {
                            pharmacyBudget.setText(snapshot.getValue().toString());
                        } else if (key.equalsIgnoreCase("currentMonth")) {

                            String month = snapshot.getValue().toString();

                            if (budgetMonth.equalsIgnoreCase(month)) {

                            } else {

                                final DialogPlus saveBudget = DialogPlus.newDialog(ShoppingCartViewActivity.this)
                                        .setContentHolder(new ViewHolder(R.layout.pop_up_customer_budget))
                                        .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                                        .setCancelable(false)
                                        .setOnBackPressListener(new OnBackPressListener() {
                                            @Override
                                            public void onBackPressed(DialogPlus dialogPlus) {
                                                dialogPlus.dismiss();
                                                startActivity(new Intent(ShoppingCartViewActivity.this, UserNavDrawer.class));
                                                finish();
                                            }
                                        })
                                        .create();
                                View v = saveBudget.getHolderView();

                                TextInputEditText beautyAndPersonalCareBudgetET = v.findViewById(R.id.beautyAndPersonalCareBudgetET);
                                TextInputEditText foodBudgetET = v.findViewById(R.id.foodBudgetET);
                                TextInputEditText homeEssentialsBudgetET = v.findViewById(R.id.homeEssentialsBudgetET);
                                TextInputEditText pharmacyBudgetET = v.findViewById(R.id.pharmacyBudgetET);
                                AppCompatButton saveBudgetBtn = v.findViewById(R.id.saveBudgetBtn);

                                saveBudget.show();

                                //remove focus
                                beautyAndPersonalCareBudgetET.clearFocus();
                                foodBudgetET.clearFocus();
                                homeEssentialsBudgetET.clearFocus();
                                pharmacyBudgetET.clearFocus();

                                saveBudgetBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //create variable for edit texts
                                        String beautyB = beautyAndPersonalCareBudgetET.getText().toString();
                                        String foodB = foodBudgetET.getText().toString();
                                        String homeB = homeEssentialsBudgetET.getText().toString();
                                        String pharmB = pharmacyBudgetET.getText().toString();

                                        if (beautyB.isEmpty() || foodB.isEmpty() || homeB.isEmpty() || pharmB.isEmpty()) {

                                            beautyAndPersonalCareBudgetET.setError("This field is required");
                                            foodBudgetET.setError("This field is required");
                                            homeEssentialsBudgetET.setError("This field is required");
                                            pharmacyBudgetET.setError("This field is required");

                                            beautyAndPersonalCareBudgetET.requestFocus();
                                            foodBudgetET.requestFocus();
                                            homeEssentialsBudgetET.requestFocus();
                                            pharmacyBudgetET.requestFocus();

                                        } else {
                                            MaterialAlertDialogBuilder budget = new MaterialAlertDialogBuilder(ShoppingCartViewActivity.this, R.style.CutShapeTheme);
                                            budget.setTitle("Save Budget");
                                            budget.setMessage("Are you sure you inputted the correct amount of your budget?");

                                            budget.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            budgetReference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                                    budgetReference.child(userID).child("beautyAndPersonalCare").push();
                                                                    budgetReference.child(userID).child("food").push();
                                                                    budgetReference.child(userID).child("homeEssentials").push();
                                                                    budgetReference.child(userID).child("pharmacy").push();
                                                                    budgetReference.child(userID).child("currentMonth").push();

                                                                    budgetReference.child(userID).child("beautyAndPersonalCare").setValue(beautyB);
                                                                    budgetReference.child(userID).child("food").setValue(foodB);
                                                                    budgetReference.child(userID).child("homeEssentials").setValue(homeB);
                                                                    budgetReference.child(userID).child("pharmacy").setValue(pharmB);
                                                                    budgetReference.child(userID).child("currentMonth").setValue(budgetMonth);
                                                                }
                                                            });

                                                            beautyAndPersonalCareBudget.setText(beautyB);
                                                            foodBudget.setText(foodB);
                                                            homeEssentialsBudget.setText(homeB);
                                                            pharmacyBudget.setText(pharmB);

                                                            dialogInterface.dismiss();
                                                            saveBudget.dismiss();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    });
                                            budget.show();
                                        }
                                    }
                                });

                            }

                        }

                    }

                } else {

                    final DialogPlus saveBudget = DialogPlus.newDialog(ShoppingCartViewActivity.this)
                            .setContentHolder(new ViewHolder(R.layout.pop_up_customer_budget))
                            .setContentBackgroundResource(R.drawable.rounded_top_for_pop_up)
                            .setCancelable(false)
                            .setOnBackPressListener(new OnBackPressListener() {
                                @Override
                                public void onBackPressed(DialogPlus dialogPlus) {
                                    dialogPlus.dismiss();
                                    startActivity(new Intent(ShoppingCartViewActivity.this, UserNavDrawer.class));
                                    finish();
                                }
                            })
                            .create();
                    View v = saveBudget.getHolderView();

                    TextInputEditText beautyAndPersonalCareBudgetET = v.findViewById(R.id.beautyAndPersonalCareBudgetET);
                    TextInputEditText foodBudgetET = v.findViewById(R.id.foodBudgetET);
                    TextInputEditText homeEssentialsBudgetET = v.findViewById(R.id.homeEssentialsBudgetET);
                    TextInputEditText pharmacyBudgetET = v.findViewById(R.id.pharmacyBudgetET);
                    AppCompatButton saveBudgetBtn = v.findViewById(R.id.saveBudgetBtn);

                    saveBudget.show();

                    //remove focus
                    beautyAndPersonalCareBudgetET.clearFocus();
                    foodBudgetET.clearFocus();
                    homeEssentialsBudgetET.clearFocus();
                    pharmacyBudgetET.clearFocus();

                    saveBudgetBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //create variable for edit texts
                            String beautyB = beautyAndPersonalCareBudgetET.getText().toString();
                            String foodB = foodBudgetET.getText().toString();
                            String homeB = homeEssentialsBudgetET.getText().toString();
                            String pharmB = pharmacyBudgetET.getText().toString();

                            if (beautyB.isEmpty() || foodB.isEmpty() || homeB.isEmpty() || pharmB.isEmpty()) {

                                beautyAndPersonalCareBudgetET.setError("This field is required");
                                foodBudgetET.setError("This field is required");
                                homeEssentialsBudgetET.setError("This field is required");
                                pharmacyBudgetET.setError("This field is required");

                                beautyAndPersonalCareBudgetET.requestFocus();
                                foodBudgetET.requestFocus();
                                homeEssentialsBudgetET.requestFocus();
                                pharmacyBudgetET.requestFocus();

                            } else {
                                MaterialAlertDialogBuilder budget = new MaterialAlertDialogBuilder(ShoppingCartViewActivity.this, R.style.CutShapeTheme);
                                budget.setTitle("Save Budget");
                                budget.setMessage("Are you sure you inputted the correct amount of your budget?");

                                budget.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                budgetReference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                        budgetReference.child(userID).child("beautyAndPersonalCare").push();
                                                        budgetReference.child(userID).child("food").push();
                                                        budgetReference.child(userID).child("homeEssentials").push();
                                                        budgetReference.child(userID).child("pharmacy").push();
                                                        budgetReference.child(userID).child("currentMonth").push();

                                                        budgetReference.child(userID).child("beautyAndPersonalCare").setValue(beautyB);
                                                        budgetReference.child(userID).child("food").setValue(foodB);
                                                        budgetReference.child(userID).child("homeEssentials").setValue(homeB);
                                                        budgetReference.child(userID).child("pharmacy").setValue(pharmB);
                                                        budgetReference.child(userID).child("currentMonth").setValue(budgetMonth);
                                                    }
                                                });

                                                beautyAndPersonalCareBudget.setText(beautyB);
                                                foodBudget.setText(foodB);
                                                homeEssentialsBudget.setText(homeB);
                                                pharmacyBudget.setText(pharmB);

                                                dialogInterface.dismiss();
                                                saveBudget.dismiss();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                budget.show();
                            }
                        }
                    });

                }


            }
        });

    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            String categoryID = categoryList.get(position).getGroceryList().get(position).getGroceryUPCA();

            String foodItemID = foodList.get(position).getGroceryUPCA();
            String beautyItemID = beautyList.get(position).getGroceryUPCA();
            String homeItemID = homeList.get(position).getGroceryUPCA();
            String pharmItemID = pharmacyList.get(position).getGroceryUPCA();

            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Customers").child("Shopping Cart")
                    .child(FirebaseAuth.getInstance().getUid());

            foodRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    for (DataSnapshot snapshot : task.getResult().getChildren()) {

                        String keyCategory = snapshot.getKey();

                        Toast.makeText(ShoppingCartViewActivity.this, keyCategory, Toast.LENGTH_SHORT).show();

                        if (keyCategory.equalsIgnoreCase("Food")) {
                            foodRef.child(keyCategory).child(categoryID).removeValue();
                        } else if (keyCategory.equalsIgnoreCase("Beauty & Personal Care")) {
                            foodRef.child(keyCategory).child(categoryID).removeValue();
                        } else if (keyCategory.equalsIgnoreCase("Home Essentials")) {
                            foodRef.child(keyCategory).child(categoryID).removeValue();
                        } else if (keyCategory.equalsIgnoreCase("Pharmacy")) {
                            foodRef.child(keyCategory).child(categoryID).removeValue();
                        }

                    }


                }
            });

            if (categoryID.equalsIgnoreCase(foodItemID)) {
                foodList.remove(position);
            } else if (categoryID.equalsIgnoreCase(beautyItemID)) {
                beautyList.remove(position);
            } else if (categoryID.equalsIgnoreCase(homeItemID)) {
                homeList.remove(position);
            } else if (categoryID.equalsIgnoreCase(pharmItemID)) {
                pharmacyList.remove(position);
            }
            main_recycler.getAdapter().notifyItemRemoved(position);

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .setSwipeLeftActionIconTint(getResources().getColor(R.color.white))
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.red))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void savedShoppingCart(DatabaseReference fromShoppingCartRef, DatabaseReference toInventoriesRef, String key) {

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        currentDate = dateFormat.format(calendar.getTime());
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = monthName[calendar.get(Calendar.MONTH)];

        String cartID = toInventoriesRef.push().getKey();

        DatabaseReference toInventories = toInventoriesRef.child(key).child(String.valueOf(currentYear)).child(String.valueOf(currentMonth))
                .child("CART" + cartID);

        if (fromShoppingCartRef.child(key).equals(null)) {
            MaterialAlertDialogBuilder noCart = new MaterialAlertDialogBuilder(ShoppingCartViewActivity.this, R.style.CutShapeTheme);
            noCart.setTitle("Save Cart");
            noCart.setMessage("Your Cart is empty, add items to save");
            noCart.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(ShoppingCartViewActivity.this, ScanBarcodeActivity.class));
                    finish();
                }
            });
            noCart.show();
        } else {
            MaterialAlertDialogBuilder saveCart = new MaterialAlertDialogBuilder(ShoppingCartViewActivity.this, R.style.CutShapeTheme);
            saveCart.setTitle("Save Cart");
            saveCart.setMessage("Are you sure you want to save this cart now?");
            //set positive/yes button
            saveCart.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            fromShoppingCartRef.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {

                                    for (DataSnapshot snap : task.getResult().getChildren()) {

                                        for (DataSnapshot snapshot : snap.getChildren()) {

                                            GroceryItemModel itemModel = snapshot.getValue(GroceryItemModel.class);
                                            toInventories.child(itemModel.getGroceryCategory()).child(itemModel.getGroceryUPCA()).setValue(itemModel);
                                        }

                                    }
                                }
                            });

                            try {
                                Thread.sleep(100);
                                fromShoppingCartRef.child(key).removeValue();
                                budgetReference.child(key).removeValue();
                                startActivity(new Intent(ShoppingCartViewActivity.this, UserNavDrawer.class));
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            saveCart.show();
        }


    }

    /*Show similar product*/
    private void similarGroceryItem(String newText) {

        List<GroceryItemModel> filteredList = new ArrayList<>();

        for (GroceryItemModel groceryListModel : groceryList) {

            if (groceryListModel.getGroceryName().toLowerCase().contains(newText.toLowerCase())) {

                filteredList.add(groceryListModel);

            }
            //groceryListAdapter.filteredGroceryList(filteredList);
        }

    }
}