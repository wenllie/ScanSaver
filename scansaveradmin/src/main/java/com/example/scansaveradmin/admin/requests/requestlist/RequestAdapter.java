package com.example.scansaveradmin.admin.requests.requestlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scansaveradmin.R;

import java.util.List;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{

    Context context;
    List<RequestListModel> requestList;

    public RequestAdapter(Context context, List<RequestListModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_request_list, parent, false);
        return new RequestAdapter.RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        RequestListModel model = requestList.get(position);

        holder.customerRequestEmail.setText(model.getCustomerEmail());
        holder.requestSubject.setText(model.getSubject());
        holder.requestDateSubmitted.setText(model.getDateSubmitted());
    }

    @Override
    public int getItemCount() {
        try {
            return requestList.size();
        } catch (Exception e) {

            return 0;
        }
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        AppCompatTextView customerRequestEmail, requestSubject, requestDateSubmitted;
        AppCompatButton researchRequestBtn, requestDeleteBtn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            customerRequestEmail = itemView.findViewById(R.id.customerRequestEmail);
            requestSubject = itemView.findViewById(R.id.requestSubject);
            requestDateSubmitted = itemView.findViewById(R.id.requestDateSubmitted);
            researchRequestBtn = itemView.findViewById(R.id.researchRequestBtn);
            requestDeleteBtn = itemView.findViewById(R.id.requestDeleteBtn);
        }
    }
}
