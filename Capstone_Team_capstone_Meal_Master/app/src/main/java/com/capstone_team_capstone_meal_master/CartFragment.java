package com.capstone_team_capstone_meal_master;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone_team_capstone_meal_master.adapter.FoodAdapter;
import com.capstone_team_capstone_meal_master.interfaces.onFoodItemClick;

import com.capstone_team_capstone_meal_master.model.Cart;
import com.capstone_team_capstone_meal_master.model.Food;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;


import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
   // LinearLayout llDiscount, llContent, llOffers;
    long points = 0;
    boolean isPointsApplied = false;
    RelativeLayout rlProgress;
  //  ProgressBar pBar;
    //  PayPalConfiguration config;
    // onOrderPlaced listener;


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


    private void updateLabels() {
        if (isPointsApplied) {
            tvDiscount.setText("-" + points);
            tvGrandTotal.setText(String.format(Locale.US, "%s %.1f", getContext().getString(R.string.dollar), grandTotal - points));
        } else {
            tvDiscount.setText("0");
            tvGrandTotal.setText(String.format(Locale.US, "%s %.1f", getContext().getString(R.string.dollar), grandTotal));
        }
    }
}
