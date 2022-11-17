package com.example.scansaverapp.users.helpcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.scansaverapp.R;

public class ExpensesAnalyticsHCActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView fromExpensesAnalyticsToHelpCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_analytics_hcactivity);

        fromExpensesAnalyticsToHelpCenter = findViewById(R.id.fromExpensesAnalyticsToHelpCenter);

        fromExpensesAnalyticsToHelpCenter.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toHelpCenter = new Intent(ExpensesAnalyticsHCActivity.this, HelpCenterActivity.class);
        toHelpCenter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toHelpCenter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toHelpCenter);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fromExpensesAnalyticsToHelpCenter:
                onBackPressed();
                break;
        }
    }
}