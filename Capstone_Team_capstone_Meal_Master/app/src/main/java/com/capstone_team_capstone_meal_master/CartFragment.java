package com.capstone_team_capstone_meal_master;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }
}