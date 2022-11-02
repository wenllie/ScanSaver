package com.example.scansaverapp.users.expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
import com.example.scansaverapp.users.settings.SettingsActivity;
import com.example.scansaverapp.users.spending.SpendingActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesAnalyticsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frExpensesToSettings;
    AppCompatSpinner spendingCategoryExSpinner;

    //analytics | pie
    PieChart monthlyExpensesPieChart;

    //analytics | bar
    BarChart monthlyExpensesBarChart;

    List<GroceryItemModel> itemList;
    ArrayList<String> monthArrayList;

    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Calendar calendar;

    float price = 0;
    int size = 0;
    float grandAve = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_analytics);

        frExpensesToSettings = findViewById(R.id.frExpensesToSettings);
        spendingCategoryExSpinner = findViewById(R.id.spendingCategoryExSpinner);
        monthlyExpensesBarChart = findViewById(R.id.monthlyExpensesBarChart);
        monthlyExpensesPieChart = findViewById(R.id.monthlyExpensesPieChart);

        //pie chart | expenses
        setUpChart();
        getData();

        //bar chart | expenses
        barchart();

        //set on click listener
        frExpensesToSettings.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(ExpensesAnalyticsActivity.this, UserNavDrawer.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frExpensesToSettings:
                onBackPressed();
                break;
        }
    }

    private void barchart() {

        itemList = new ArrayList<>();
        monthArrayList = new ArrayList<>();

        ArrayList<BarEntry> months = new ArrayList<>();

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());

        spendingReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                itemList.clear();
                monthArrayList.clear();

                for (DataSnapshot yearSnap : task.getResult().getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {

                        String monthKey = monthSnap.getKey();

                        for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                            for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                for (DataSnapshot snap : idSnap.getChildren()) {

                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                    itemList.add(groceryItemModel);

                                }
                            }
                        }

                        monthArrayList.add(monthKey);
                        if (monthKey.equalsIgnoreCase("January")) {
                            int month = 1;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("February")) {
                            int month = 2;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("March")) {
                            int month = 3;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("April")) {
                            int month = 4;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("May")) {
                            int month = 5;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("June")) {
                            int month = 6;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("July")) {
                            int month = 7;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("August")) {
                            int month = 8;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("September")) {
                            int month = 9;

                            for (int i = 0; i < itemList.size(); i++) {
                                grandAve += Float.parseFloat(itemList.get(i).getGroceryTotalItemPrice());
                                //Toast.makeText(ExpensesAnalyticsActivity.this, String.valueOf(itemList.get(i).getGroceryTotalItemPrice()) + "price", Toast.LENGTH_SHORT).show();
                            }
                            months.add(new BarEntry(month, grandAve));
                            Toast.makeText(ExpensesAnalyticsActivity.this, String.valueOf(grandAve) + monthKey, Toast.LENGTH_SHORT).show();
                        } else if (monthKey.equalsIgnoreCase("October")) {
                            int month = 10;

                            for (int i = 0; i < itemList.size(); i++) {
                                grandAve += Float.parseFloat(itemList.get(i).getGroceryTotalItemPrice());
                                //Toast.makeText(ExpensesAnalyticsActivity.this, String.valueOf(itemList.get(i).getGroceryTotalItemPrice()) + "price", Toast.LENGTH_SHORT).show();
                            }
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("November")) {
                            int month = 11;
                            months.add(new BarEntry(month, grandAve));
                        } else if (monthKey.equalsIgnoreCase("December")) {
                            int month = 12;
                            months.add(new BarEntry(month, grandAve));
                        }

                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.COLORFUL_COLORS){
                    colors.add(color);
                }

                for (int color : ColorTemplate.MATERIAL_COLORS){
                    colors.add(color);
                }

                for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.JOYFUL_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.LIBERTY_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.PASTEL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                BarDataSet barDataSet = new BarDataSet(months, "Month");
                barDataSet.setColors(colors);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);

                monthlyExpensesBarChart.setFitBars(true);
                monthlyExpensesBarChart.setData(barData);
                monthlyExpensesBarChart.getDescription().setTextColor(R.color.blue_main);
                monthlyExpensesBarChart.getDescription().setText("Category: Month");

                XAxis axis = monthlyExpensesBarChart.getXAxis();
                axis.setValueFormatter(new IndexAxisValueFormatter(monthArrayList));
                axis.setPosition(XAxis.XAxisPosition.TOP);
                axis.setDrawGridLines(false);
                axis.setDrawAxisLine(false);
                axis.setGranularity(1f);
                axis.setLabelCount(monthArrayList.size());
                axis.setLabelRotationAngle(270);
                monthlyExpensesBarChart.animateY(2000);
                monthlyExpensesBarChart.invalidate();
            }
        });

    }

    //set up pie chart for monthly expenses
    private void setUpChart() {

        monthlyExpensesPieChart.setDrawHoleEnabled(true);
        monthlyExpensesPieChart.setUsePercentValues(true);
        monthlyExpensesPieChart.setEntryLabelTextSize(12f);
        monthlyExpensesPieChart.setCenterText("Monthly Expenses");
        monthlyExpensesPieChart.setCenterTextSize(24);
        monthlyExpensesPieChart.getDescription().setEnabled(false);

        Legend legend = monthlyExpensesPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);

    }

    //get the data in firebase to be displayed in pie chart
    private void getData() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> entries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                            for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                for (DataSnapshot snap : idSnap.getChildren()) {

                                    GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                    itemList.add(groceryItemModel);
                                    price = Float.valueOf(groceryItemModel.getGroceryTotalItemPrice());


                                }
                            }
                        }

                        for (int i = 0; i < itemList.size(); i++) {
                            grandAve += Float.parseFloat(String.valueOf(price));
                        }
                        Toast.makeText(ExpensesAnalyticsActivity.this, String.valueOf(grandAve) + "pie" + monthKey, Toast.LENGTH_SHORT).show();
                        float totalGrandAve = grandAve + grandAve;
                        float percentTotal = (grandAve / totalGrandAve) * 100;

                        entries.add(new PieEntry(percentTotal, monthKey));

                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.COLORFUL_COLORS){
                    colors.add(color);
                }

                for (int color : ColorTemplate.MATERIAL_COLORS){
                    colors.add(color);
                }

                for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.JOYFUL_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.LIBERTY_COLORS) {
                    colors.add(color);
                }

                for (int color : ColorTemplate.PASTEL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(entries, "Categories");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(monthlyExpensesPieChart));
                data.setValueTextSize(12f);

                monthlyExpensesPieChart.setData(data);
                monthlyExpensesPieChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}