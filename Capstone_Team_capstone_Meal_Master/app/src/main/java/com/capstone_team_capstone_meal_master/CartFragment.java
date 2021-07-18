package com.capstone_team_capstone_meal_master;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone_team_capstone_meal_master.adapter.FoodAdapter;
import com.capstone_team_capstone_meal_master.model.Cart;
import com.capstone_team_capstone_meal_master.model.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CartFragment extends Fragment {

    Cart cart;
    List<Food> foodItems;
    double total = 0, grandTotal = 0;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FoodAdapter foodAdapter;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    TextView tvItemTotal, tvGrandTotal, tvDiscountPoints, tvDiscount, tvNoItems;
    Button btApplyPoints, btRemovePoints, btCheckout;
    LinearLayout llDiscount, llContent, llOffers;
    long points = 0;
    boolean isPointsApplied = false;
    RelativeLayout rlProgress;
    ProgressBar pBar;


    public CartFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cart = new Cart();
        foodItems = new ArrayList<>();
        foodAdapter = new FoodAdapter(getActivity(), foodItems, (position, mode) -> {
            Food food = foodItems.get(position);
            if (currentUser != null) {
                String uuid = currentUser.getUid();
                HashMap<String, Object> map = new HashMap<>();
                if (mode == 1) {
                    map.put(food.getId(), FieldValue.increment(1));
                } else {
                    map.put(food.getId(), FieldValue.increment(-1));
                }
                firebaseFirestore.collection("cart").document(uuid).set(map, SetOptions.merge());
                updateLabels();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        tvGrandTotal = view.findViewById(R.id.tvGrandTotal);
        tvItemTotal = view.findViewById(R.id.tvTotal);
        tvDiscountPoints = view.findViewById(R.id.tvDiscountPoints);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        btApplyPoints = view.findViewById(R.id.btDiscountApply);
        btRemovePoints = view.findViewById(R.id.btDiscountRemove);
        llDiscount = view.findViewById(R.id.llDiscount);
        btCheckout = view.findViewById(R.id.btnCheckout);
        tvNoItems = view.findViewById(R.id.tvNoItems);
        rlProgress = view.findViewById(R.id.rlProgress);
        llContent = view.findViewById(R.id.llContent);
        pBar = view.findViewById(R.id.pBar);
        llOffers = view.findViewById(R.id.llOffers);
        btCheckout.setOnClickListener(v -> {
            long point = isPointsApplied ? points : 0;
            PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(grandTotal - point)), "USD", "New Order",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(getContext(), PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            launcher.launch(intent);
        });
        btApplyPoints.setOnClickListener(v -> {
            isPointsApplied = true;
            btApplyPoints.setVisibility(View.GONE);
            btRemovePoints.setVisibility(View.VISIBLE);
            updateLabels();
        });
        btRemovePoints.setOnClickListener(v -> {
            isPointsApplied = false;
            btRemovePoints.setVisibility(View.GONE);
            btApplyPoints.setVisibility(View.VISIBLE);
            updateLabels();
        });
        RecyclerView rvCart = view.findViewById(R.id.rvCart);
        rvCart.setNestedScrollingEnabled(true);
        rvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCart.setAdapter(foodAdapter);
        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(getString(R.string.paypal_key));
        fetchCartData();
        return view;
    }


    private void updateLabels() {
        if (isPointsApplied) {
            tvDiscount.setText("-" + points);
            tvGrandTotal.setText(String.format(Locale.US, "%s %.1f", getContext().getString(R.string.dollar), grandTotal - points));
        } else {
            tvDiscount.setText("0");
            tvGrandTotal.setText(String.format(Locale.US, "%s %.1f", getContext().getString(R.string.dollar), grandTotal));
        }
    }

    public void fetchCartData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (!currentUser.isAnonymous()) {
                firebaseFirestore.collection("discount").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().contains("points")) {
                        points = Math.round(Double.parseDouble(String.valueOf(task.getResult().get("points"))));
                        tvDiscountPoints.setText(String.format(getString(R.string.available_points), points));
                        toggleDiscount(points == 0);
                    } else {
                        toggleDiscount(false);
                    }
                });
            } else {
                llDiscount.setVisibility(View.GONE);
                llOffers.setVisibility(View.GONE);
            }
            firebaseFirestore.collection("cart").document(uid).addSnapshotListener((value, error) -> {
                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null && data.size() > 0) {
                        firebaseFirestore
                                .collection("food")
                                .whereIn("id", Arrays.asList(data.keySet().toArray()))
                                .get()
                                .addOnCompleteListener(task -> {
                                    pBar.setVisibility(View.GONE);
                                    foodItems.clear();
                                    total = grandTotal = 0;
                                    Map<Food, Integer> map = new HashMap<>();
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        for (DocumentSnapshot ds : task.getResult().getDocuments()) {
                                            Food food = ds.toObject(Food.class);
                                            if (food != null) {
                                                int quantity = Integer.parseInt(String.valueOf(data.get(food.getId())));
                                                if (quantity > 0) {
                                                    foodItems.add(food);
                                                    map.put(food, quantity);
                                                    total += food.getPrice() * quantity;
                                                    grandTotal = total;
                                                }
                                            }
                                        }
                                    }
                                    cart.setCartItems(map);
                                    foodAdapter.notifyDataSetChanged();
                                    tvItemTotal.setText(String.format(Locale.US, "%s %.1f", getContext().getString(R.string.dollar), total));
                                    updateLabels();
                                    if (foodItems.size() > 0) {
                                        rlProgress.setVisibility(View.GONE);
                                        llContent.setVisibility(View.VISIBLE);
                                    } else {
                                        showEmptyCart();
                                    }
                                });
                    } else {
                        showEmptyCart();
                    }
                } else {
                    showEmptyCart();
                }
            });
        }

    }
    private void toggleDiscount(boolean flag) {
        if (flag) {
            btRemovePoints.setVisibility(View.GONE);
            btApplyPoints.setVisibility(View.GONE);
            tvDiscountPoints.setText(R.string.no_points);
        } else {
            btApplyPoints.setVisibility(View.VISIBLE);
        }
    }
    public void showEmptyCart() {

            pBar.setVisibility(View.GONE);
            rlProgress.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
            tvNoItems.setVisibility(View.VISIBLE);

    }

}