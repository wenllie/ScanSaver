package com.example.scansaverapp.users.expenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.core.ui.ChartCredits;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.helpers_database.GroceryItemModel;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesAnalyticsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frExpensesToSettings;
    AppCompatSpinner spendingCategoryExSpinner;
    AppCompatTextView monthChosen;

    //spending chart
    AnyChartView anyChartView;
    PieChart drilledDownChart;

    List<GroceryItemModel> itemList;
    ArrayList<String> monthArrayList;
    ArrayList<String> yearList;

    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_analytics);

        frExpensesToSettings = findViewById(R.id.frExpensesToSettings);
        spendingCategoryExSpinner = findViewById(R.id.spendingCategoryExSpinner);
        anyChartView = findViewById(R.id.anychart);
        drilledDownChart = findViewById(R.id.drilledDownChart);
        monthChosen = findViewById(R.id.monthChosen);

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        yearList = new ArrayList<>();

        /*Setting up the spinner data*/
        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yearList.clear();

                for (DataSnapshot yearSnap : snapshot.getChildren()) {
                    String yearKey = yearSnap.getKey();
                    yearList.add(yearKey);
                }

                ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(ExpensesAnalyticsActivity.this, R.layout.expenses_year_list, yearList);
                yearAdapter.setDropDownViewResource(R.layout.expenses_year_list);
                spendingCategoryExSpinner.setAdapter(yearAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spendingCategoryExSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spendingChart();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    public float groceryTotals() {
        float totalAve = 0;
        for (int i = 0; i < itemList.size(); i++) {
            totalAve += Float.parseFloat(itemList.get(i).getGroceryTotalItemPrice());
        }

        return totalAve;
    }

    private void spendingChart() {

        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Pie pie = AnyChart.pie3d();
        ChartCredits chartCredits = pie.credits();
        chartCredits.enabled(false);
        pie.innerRadius("30%");
        pie.radius("85%");

        itemList = new ArrayList<>();

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());


        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot yearSnap : snapshot.getChildren()) {
                    List<DataEntry> dataEntries = new ArrayList<>();

                    String yearKey = yearSnap.getKey();

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("January")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {


                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("February")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("March")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("April")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("May")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("June")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("July")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("August")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("September")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("September")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("October")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("November")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        } else if (monthKey.equalsIgnoreCase("December")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    for (DataSnapshot snap : idSnap.getChildren()) {

                                        GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                        itemList.add(groceryItemModel);

                                    }
                                }
                            }

                            dataEntries.add(new ValueDataEntry(monthKey, groceryTotals()));

                        }

                        pie.title(yearKey + " Grocery Expenses");

                    }

                    pie.data(dataEntries);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value", "drillDown"}) {
            @Override
            public void onClick(Event event) {
                //Toast.makeText(ExpensesAnalyticsActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();

                spendingReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot yearSnap : snapshot.getChildren()) {

                            for (DataSnapshot monthSnap : yearSnap.getChildren()) {

                                String monthKey = monthSnap.getKey();

                                pie.getSelectedPoints();

                                if (monthKey.equalsIgnoreCase(event.getData().get("x"))) {

                                    if (event.getData().get("x").equalsIgnoreCase("January")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        januaryDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("February")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        februaryDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("March")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        marchDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("April")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        aprilDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("May")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        mayDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("June")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        juneDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("July")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        julyDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("August")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        augustDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("September")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        septemberDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("October")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        octoberDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("November")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        novemberDrilledDownChart();

                                    } else if (event.getData().get("x").equalsIgnoreCase("December")) {

                                        monthChosen.setText(event.getData().get("x"));
                                        drilledDownChart.setVisibility(View.VISIBLE);
                                        setUpChart();
                                        decemberDrilledDownChart();

                                    }

                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        pie.labels().position("outside");

        pie.legend().title().enabled(false);
        pie.legend().title()
                .text("Category")
                .padding(0d, 0d, 5d, 0d);

        pie.legend()
                .position("bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

    }

    //set up pie chart for monthly expenses
    private void setUpChart() {

        drilledDownChart.setDrawHoleEnabled(false);
        drilledDownChart.setUsePercentValues(true);
        drilledDownChart.setDrawEntryLabels(false);
        drilledDownChart.getDescription().setEnabled(false);

        Legend legend = drilledDownChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(13f);
        legend.setDrawInside(false);
        legend.setEnabled(true);

    }

    //get the data in firebase to be displayed in pie chart | December Month
    private void decemberDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("December")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | November Month
    private void novemberDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("November")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | October Month
    private void octoberDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("October")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //get the data in firebase to be displayed in pie chart | September Month
    private void septemberDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("September")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | August Month
    private void augustDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("August")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | July Month
    private void julyDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("July")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | June Month
    private void juneDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("June")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | May Month
    private void mayDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("May")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | April Month
    private void aprilDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("April")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | March Month
    private void marchDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("March")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | February Month
    private void februaryDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("February")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get the data in firebase to be displayed in pie chart | January Month
    private void januaryDrilledDownChart() {

        DatabaseReference spendingReference = FirebaseDatabase.getInstance().getReference().child("Customers").child("Inventories").child(FirebaseAuth.getInstance().getUid());
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        itemList = new ArrayList<>();

        spendingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot yearSnap : snapshot.getChildren()) {

                    for (DataSnapshot monthSnap : yearSnap.getChildren()) {
                        String monthKey = monthSnap.getKey();

                        if (monthKey.equalsIgnoreCase("January")) {
                            itemList.clear();

                            for (DataSnapshot cartSnap : monthSnap.getChildren()) {

                                for (DataSnapshot idSnap : cartSnap.getChildren()) {

                                    String categoryKey = idSnap.getKey();

                                    if (categoryKey.equalsIgnoreCase("Food")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Beauty & Personal Care")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Home Essentials")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    } else if (categoryKey.equalsIgnoreCase("Pharmacy")) {

                                        for (DataSnapshot snap : idSnap.getChildren()) {

                                            GroceryItemModel groceryItemModel = snap.getValue(GroceryItemModel.class);
                                            itemList.add(groceryItemModel);

                                        }

                                        pieEntries.add(new PieEntry(groceryTotals(), categoryKey));

                                    }


                                }
                            }

                        }
                    }

                }

                //applying colors
                ArrayList<Integer> colors = new ArrayList<>();
                for (int color : ColorTemplate.MATERIAL_COLORS) {
                    colors.add(color);
                }

                colors.add(ColorTemplate.getHoloBlue());

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(drilledDownChart));
                data.setValueTextSize(16f);

                drilledDownChart.setData(data);
                drilledDownChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}