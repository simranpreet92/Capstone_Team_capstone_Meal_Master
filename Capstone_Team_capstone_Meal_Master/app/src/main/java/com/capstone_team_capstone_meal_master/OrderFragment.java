package com.capstone_team_capstone_meal_master;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.capstone_team_capstone_meal_master.adapter.OrderAdapter;
import com.capstone_team_capstone_meal_master.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    ProgressBar pBar;
    RecyclerView rvOrder;
    TextView tvNoOrder;
    List<Order> orders = new ArrayList<>();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    OrderAdapter orderAdapter;

    public OrderFragment() {
        // Required empty public constructor
    }


    public static OrderFragment newInstance() {

        return new OrderFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchOrders();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);
        pBar = view.findViewById(R.id.pBar);
        rvOrder = view.findViewById(R.id.rvOrder);
        tvNoOrder = view.findViewById(R.id.tvNoOrder);
        orderAdapter = new OrderAdapter(getContext(), orders);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rvOrder.setAdapter(orderAdapter);
        return view;
    }

    private void fetchOrders() {
        if (currentUser != null) {
            firebaseFirestore
                    .collection("order")
                    .whereEqualTo("uid", currentUser.getUid())
                    .addSnapshotListener((value, error) -> {
                        pBar.setVisibility(View.GONE);
                        orders.clear();
                        if (value != null && !value.isEmpty()) {
                            rvOrder.setVisibility(View.VISIBLE);
                            tvNoOrder.setVisibility(View.GONE);
                            List<DocumentSnapshot> documents = value.getDocuments();
                            for (DocumentSnapshot ds : documents) {
                                Order order = ds.toObject(Order.class);
                                if (order != null) {
                                    orders.add(order);
                                }
                                orderAdapter.notifyDataSetChanged();
                            }
                            if (orders.isEmpty()) {
                                tvNoOrder.setVisibility(View.VISIBLE);
                                rvOrder.setVisibility(View.GONE);
                            }
                        } else {
                            tvNoOrder.setVisibility(View.VISIBLE);
                            rvOrder.setVisibility(View.GONE);

                        }

                    });
        }
    }
}