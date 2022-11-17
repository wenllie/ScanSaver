package com.example.scansaverapp.users.helpcenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.users.settings.CardViewRateUs;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HelpCenterActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView frHelpCenterToDashboard;
    AppCompatButton submitRequestBtn, fileComplaintBtn;
    CardView rateUsCardView, manageAccountCardView, shoppingCartCardView, monthlyBudgetCardView, expensesAnalyticsCardView, grocerySpendingCardView;
    private MaterialAlertDialogBuilder requestDialog, complaintsDialog;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        frHelpCenterToDashboard = (ImageView) findViewById(R.id.frHelpCenterToDashboard);
        /*fileComplaintBtn = (AppCompatButton) findViewById(R.id.fileComplaintBtn);
        submitRequestBtn = (AppCompatButton) findViewById(R.id.submitRequestBtn);*/
        rateUsCardView = findViewById(R.id.rateUsCardView);
        manageAccountCardView = findViewById(R.id.manageAccountCardView);
        shoppingCartCardView = findViewById(R.id.shoppingCartCardView);
        monthlyBudgetCardView = findViewById(R.id.monthlyBudgetCardView);
        expensesAnalyticsCardView = findViewById(R.id.expensesAnalyticsCardView);
        grocerySpendingCardView = findViewById(R.id.grocerySpendingCardView);

        frHelpCenterToDashboard.setOnClickListener(this);
        /*submitRequestBtn.setOnClickListener(this);
        fileComplaintBtn.setOnClickListener(this);*/
        rateUsCardView.setOnClickListener(this);
        manageAccountCardView.setOnClickListener(this);
        shoppingCartCardView.setOnClickListener(this);
        monthlyBudgetCardView.setOnClickListener(this);
        expensesAnalyticsCardView.setOnClickListener(this);
        grocerySpendingCardView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent toDashboard = new Intent(HelpCenterActivity.this, UserNavDrawer.class);
        toDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toDashboard);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frHelpCenterToDashboard:
                onBackPressed();
                break;

            case R.id.manageAccountCardView:
                startActivity(new Intent(HelpCenterActivity.this, ManageAccountHCActivity.class));
                finish();
                break;

            case R.id.shoppingCartCardView:
                startActivity(new Intent(HelpCenterActivity.this, ShoppingCartHCActivity.class));
                finish();
                break;

            case R.id.monthlyBudgetCardView:
                startActivity(new Intent(HelpCenterActivity.this, MonthlyBudgetHCActivity.class));
                finish();
                break;

            case R.id.expensesAnalyticsCardView:
                startActivity(new Intent(HelpCenterActivity.this, ExpensesAnalyticsHCActivity.class));
                finish();
                break;

            case R.id.grocerySpendingCardView:
                startActivity(new Intent(HelpCenterActivity.this, GrocerySpendingHCActivity.class));
                finish();
                break;

            /*case R.id.submitRequestBtn:
                submitRequest();
                break;

            case R.id.fileComplaintBtn:
                submitComplaint();
                break;*/

            case R.id.rateUsCardView:
                rateApp();
                break;
        }
    }

    private void rateApp() {

        CardViewRateUs rateDialog = new CardViewRateUs(HelpCenterActivity.this);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        rateDialog.setCancelable(false);
        rateDialog.show();

    }



    /*private void submitComplaint() {

        LayoutInflater inflater = getLayoutInflater();
        View com_layout = inflater.inflate(R.layout.dialog_box_complaints, null);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        complaintsDialog = new MaterialAlertDialogBuilder(HelpCenterActivity.this, R.style.CutShapeTheme);

        complaintsDialog.setTitle("File a Complaint");
        complaintsDialog.setView(com_layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        TextInputEditText complaintSubject = com_layout.findViewById(R.id.complaintSubject);
                        TextInputEditText complaintMessage = com_layout.findViewById(R.id.complaintMessage);

                        String subject = complaintSubject.getText().toString();
                        String message = complaintMessage.getText().toString();

                        if (subject.isEmpty()) {
                            complaintSubject.setError("This field is required!");
                            complaintSubject.requestFocus();
                            return;
                        } else if (message.isEmpty()) {
                            complaintMessage.setError("This field is required!");
                            complaintMessage.requestFocus();
                            return;
                        } else {

                            ComplaintHelper complaintHelper = new ComplaintHelper(subject, message, currentDate, FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid());

                            DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Customers").child("Complaints");

                            requestReference.child(currentDate).setValue(complaintHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HelpCenterActivity.this, "Submitted successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HelpCenterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    private void submitRequest() {

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        LayoutInflater inflater = getLayoutInflater();
        View req_layout = inflater.inflate(R.layout.dialog_box_request, null);

        requestDialog = new MaterialAlertDialogBuilder(HelpCenterActivity.this, R.style.CutShapeTheme);

        requestDialog.setTitle("Submit a Request");
        requestDialog.setView(req_layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        TextInputEditText requestSubject = req_layout.findViewById(R.id.requestSubject);
                        TextInputEditText requestMessage = req_layout.findViewById(R.id.requestMessage);

                        String subject = requestSubject.getText().toString();
                        String message = requestMessage.getText().toString();

                        if (subject.isEmpty()) {
                            requestSubject.setError("This field is required!");
                            requestSubject.requestFocus();
                            return;
                        } else if (message.isEmpty()) {
                            requestMessage.setError("This field is required!");
                            requestMessage.requestFocus();
                            return;
                        } else {

                            RequestHelper requestHelper = new RequestHelper(subject, message, currentDate, FirebaseAuth.getInstance().getCurrentUser().getEmail(), FirebaseAuth.getInstance().getUid());

                            DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Customers").child("Requests");

                            requestReference.child(currentDate).setValue(requestHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(HelpCenterActivity.this, "Submitted successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HelpCenterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }*/
}