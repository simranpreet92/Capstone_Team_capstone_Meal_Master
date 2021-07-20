package com.capstone_team_capstone_meal_master;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone_team_capstone_meal_master.adapter.FoodAdapter;
import com.capstone_team_capstone_meal_master.interfaces.onFoodItemClick;
import com.capstone_team_capstone_meal_master.model.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoryFragment extends Fragment {

    private ArrayList<Food> foodItems;
    private ArrayList<Food> filteredFoodItems;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FoodAdapter foodAdapter;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    TextView tvAvailableItems;

    public CategoryFragment() {
    }

//    public static CategoryFragment newInstance() {
//        return new CategoryFragment();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foodItems = new ArrayList<>();
        filteredFoodItems = new ArrayList<>();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        foodAdapter = new FoodAdapter(getActivity(), filteredFoodItems, (position, mode) -> {
            Food food = filteredFoodItems.get(position);
            if (currentUser != null) {
                String uuid = currentUser.getUid();
                HashMap<String, Object> map = new HashMap<>();
                if (mode == 1) {
                    map.put(food.getId(), FieldValue.increment(1));
                } else {
                    map.put(food.getId(), FieldValue.increment(-1));
                }
                firebaseFirestore.collection("cart").document(uuid).set(map, SetOptions.merge());
            }
        });
        fetchCategoryData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);
        EditText etSearch = view.findViewById(R.id.etSearch);
        tvAvailableItems = view.findViewById(R.id.tvAvailableItems);
        etSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textVisible = s.toString().trim().toLowerCase();
                filteredFoodItems.clear();
                for (Food f : foodItems) {
                    if (textVisible.isEmpty() || f.getName().contains(textVisible)) {
                        filteredFoodItems.add(f);
                    }
                }
                if (filteredFoodItems.isEmpty())
                    Toast.makeText(getContext(), "No Items to show", Toast.LENGTH_SHORT).show();
                tvAvailableItems.setText(String.format(Locale.CANADA, "Displaying %d items", filteredFoodItems.size()));
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        RecyclerView rvFoodItems = view.findViewById(R.id.rvFoodItems);
        rvFoodItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFoodItems.setAdapter(foodAdapter);
        return view;
    }

    public void fetchCategoryData() {

        firebaseFirestore.collection("food").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            if (value != null) {
                for (DocumentSnapshot ds : value.getDocuments()) {
                    Food food = ds.toObject(Food.class);
                    foodItems.add(food);
                }
                Collections.sort(foodItems, (f1, f2) -> f1.getCategory().compareTo(f2.getCategory()));
                filteredFoodItems.addAll(foodItems);
                foodAdapter.notifyDataSetChanged();
                tvAvailableItems.setText(String.format(Locale.CANADA, "Displaying %d items", filteredFoodItems.size()));

            }
        });
    }


}