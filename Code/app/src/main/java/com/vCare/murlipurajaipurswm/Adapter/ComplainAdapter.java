package com.vCare.murlipurajaipurswm.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vCare.murlipurajaipurswm.Model.ComplainModel;
import com.vCare.murlipurajaipurswm.R;

import java.util.ArrayList;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<ComplainModel> models;

    public ComplainAdapter(Context context, ArrayList<ComplainModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public ComplainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.complain_rv_activity,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainAdapter.ViewHolder holder, int position) {
        ComplainModel count = models.get(position);

        holder.message.setText(count.getmessage());
        holder.att_date.setText(count.getDate());
        holder.att_type.setText(count.getType());

        if (count.status.equals("1")){
            holder.att_action.setTextColor(Color.RED);
            holder.att_action.setText("Open");
        }else if (count.status.equals("2")){
            holder.att_action.setTextColor(Color.BLUE);
            holder.att_action.setText("InProgress");
        }else {
            holder.att_action.setTextColor(Color.GREEN);
            holder.att_action.setText("Done");
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView message;
        private final TextView att_date;
        private final TextView att_type;
        private final TextView att_action;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            att_date = itemView.findViewById(R.id.att_date);
            att_type = itemView.findViewById(R.id.att_type);
            att_action = itemView.findViewById(R.id.att_action);
        }
    }
}
