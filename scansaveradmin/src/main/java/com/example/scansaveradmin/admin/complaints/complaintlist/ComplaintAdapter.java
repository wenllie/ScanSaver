package com.example.scansaveradmin.admin.complaints.complaintlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaveradmin.R;
import com.example.scansaveradmin.admin.requests.requestlist.RequestAdapter;
import com.example.scansaveradmin.admin.requests.requestlist.RequestListModel;

import java.util.List;


public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>{

    Context context;
    List<ComplaintListModel> complaintList;

    public ComplaintAdapter(Context context, List<ComplaintListModel> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_complaint_list, parent, false);
        return new ComplaintAdapter.ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {

        ComplaintListModel model = complaintList.get(position);

        holder.customerComplaintEmail.setText(model.getCustomerEmail());
        holder.complaintSubject.setText(model.getSubject());
        holder.complaintDateSubmitted.setText(model.getDateSubmitted());
    }

    @Override
    public int getItemCount() {
        try {
            return complaintList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public class ComplaintViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView customerComplaintEmail, complaintSubject, complaintDateSubmitted;
        AppCompatButton researchComplaintBtn, complaintDeleteBtn;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);

            customerComplaintEmail = itemView.findViewById(R.id.customerComplaintEmail);
            complaintSubject = itemView.findViewById(R.id.complaintSubject);
            complaintDateSubmitted = itemView.findViewById(R.id.complaintDateSubmitted);
            researchComplaintBtn = itemView.findViewById(R.id.researchComplaintBtn);
            complaintDeleteBtn = itemView.findViewById(R.id.complaintDeleteBtn);
        }
    }
}
