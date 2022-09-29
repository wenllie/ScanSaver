package com.example.scansaverapp.users.spending;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;

public class SpendingActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView frSpendingToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);

        frSpendingToDashboard = (ImageView) findViewById(R.id.frSpendingToDashboard);

        frSpendingToDashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frSpendingToDashboard:
                Intent toDashboard = new Intent(SpendingActivity.this, UserNavDrawer.class);
                startActivity(toDashboard);
                break;
        }
    }
}