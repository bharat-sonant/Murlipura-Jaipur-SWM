package com.vCare.murlipurajaipurswm.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vCare.murlipurajaipurswm.Model.ScanCardDataModel;
import com.vCare.murlipurajaipurswm.R;

import java.util.ArrayList;

public class CardDataAdapter extends RecyclerView.Adapter<CardDataAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<ScanCardDataModel> arrayList;

    public CardDataAdapter(Context context, ArrayList<ScanCardDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CardDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scancardrv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardDataAdapter.ViewHolder holder, int position) {
        holder.dateNoTv.setText(arrayList.get(position).getDate());
        holder.scanTimeTv.setText(arrayList.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateNoTv;
        private final TextView scanTimeTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateNoTv = itemView.findViewById(R.id.dateNoTv);
            scanTimeTv = itemView.findViewById(R.id.scanTimeTv);
        }
    }
}
