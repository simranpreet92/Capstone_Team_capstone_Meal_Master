package com.capstone_team_capstone_meal_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone_team_capstone_meal_master.R;
import com.capstone_team_capstone_meal_master.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;



public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

   Context context;
    List<Order> orders;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.single_row_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderTotal, tvOrderId, tvOrderItems, tvOrderedOn, tvPaymentId, tvConfirmed, tvIncomplete;

        public OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderedOn = itemView.findViewById(R.id.tvOrderedOn);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderItems = itemView.findViewById(R.id.tvItems);
            tvPaymentId = itemView.findViewById(R.id.tvPaymentId);
            tvConfirmed = itemView.findViewById(R.id.tvConfirmed);
            tvIncomplete = itemView.findViewById(R.id.tvIncomplete);
        }
    }
}
