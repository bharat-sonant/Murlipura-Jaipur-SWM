package com.vCare.murlipurajaipurswm.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.vCare.murlipurajaipurswm.Model.PaymentHistoryModel;
import com.vCare.murlipurajaipurswm.R;

import java.util.ArrayList;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PaymentHistoryModel> models;

    public PaymentHistoryAdapter(Context context, ArrayList<PaymentHistoryModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_rcy_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentHistoryModel modelPosition = models.get(position);

        holder.tv_date.setText("Date: "+modelPosition.getTransactionDateTime());
        holder.tv_payMethod.setText("Via: "+modelPosition.getPayMethod());
        holder.tv_amount.setText("Amount: ₹"+modelPosition.getTransactionAmount());
        holder.tv_orderId.setText("OrderId: "+modelPosition.getRetrievalReferenceNo());
        holder.tv_collectorId.setText("Collected By: "+modelPosition.getpaymentCollectionByName());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_payMethod;
        private final TextView tv_date;
        private final TextView tv_amount;
        private final TextView tv_orderId;
        private final TextView tv_collectorId;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_payMethod = itemView.findViewById(R.id.tv_payMethod);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            tv_orderId = itemView.findViewById(R.id.tv_orderId);
            tv_collectorId = itemView.findViewById(R.id.tv_collectorId);
        }
    }
}
