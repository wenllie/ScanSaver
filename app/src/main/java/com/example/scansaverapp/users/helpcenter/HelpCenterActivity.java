package com.example.scansaverapp.users.helpcenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.scansaverapp.R;
import com.example.scansaverapp.UserNavDrawer;
import com.example.scansaverapp.users.settings.CardViewRateUs;
import com.example.scansaverapp.users.settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HelpCenterActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView frHelpCenterToDashboard;
    AppCompatButton submitRequestBtn, fileComplaintBtn;
    CardView rateUs;
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
        rateUs = findViewById(R.id.rateUs);

        frHelpCenterToDashboard.setOnClickListener(this);
        /*submitRequestBtn.setOnClickListener(this);
        fileComplaintBtn.setOnClickListener(this);*/
        rateUs.setOnClickListener(this);
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

            /*case R.id.submitRequestBtn:
                submitRequest();
                break;

            case R.id.fileComplaintBtn:
                submitComplaint();
                break;*/

            case R.id.rateUs:
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