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


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    List<Order> orders;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy, MMMM dd EEEE hh:mm a", Locale.CANADA);

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.single_row_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        if (order.getStatus().equalsIgnoreCase("confirmed")) {
            holder.tvConfirmed.setVisibility(View.VISIBLE);
            holder.tvIncomplete.setVisibility(View.GONE);
        } else {
            holder.tvConfirmed.setVisibility(View.GONE);
            holder.tvIncomplete.setVisibility(View.VISIBLE);
        }
        holder.tvPaymentId.setText(order.getPaymentId());
        holder.tvOrderId.setText(String.valueOf(order.getOrder_id()));
        holder.tvOrderedOn.setText(formatter.format(order.getTimestamp().toDate()));
        holder.tvOrderTotal.setText(String.format(Locale.US, "%s %.1f", context.getString(R.string.dollar), order.getGrandTotal()));
        Map<String, Integer> foodItems = order.getFoodItems();
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> map : foodItems.entrySet()) {
            String key = map.getKey();
            firebaseFirestore
                    .collection("food")
                    .document(key)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null)
                        {
                            String name = String.valueOf(task.getResult().get("name"));
                            stringBuilder.append(String.format(Locale.CANADA, "%d X %s ", map.getValue(), name));
                            holder.tvOrderItems.setText(stringBuilder.toString());
                        }
                    });
                        }
                    }

    @Override
    public int getItemCount() {
        {
            return orders.size();
        }
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
