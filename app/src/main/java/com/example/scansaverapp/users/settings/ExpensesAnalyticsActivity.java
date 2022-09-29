package com.example.scansaverapp.users.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.scansaverapp.R;

public class ExpensesAnalyticsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView frExpensesToSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_analytics);

        frExpensesToSettings = (ImageView) findViewById(R.id.frExpensesToSettings);

        frExpensesToSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frExpensesToSettings:
                startActivity(new Intent(ExpensesAnalyticsActivity.this, SettingsActivity.class));
                break;
        }
    }
}