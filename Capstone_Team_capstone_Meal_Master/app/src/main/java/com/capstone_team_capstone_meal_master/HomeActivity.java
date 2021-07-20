package com.capstone_team_capstone_meal_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser user;
    FrameLayout flContent;
    Fragment categoryFragment, orderFragment, cartFragment, activeFragment;
    Button btLogout;
    String uid;
    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btLogout = findViewById(R.id.btLogout);
        user = auth.getCurrentUser();
        if (user == null) {
            btLogout.setVisibility(View.GONE);
            return;
        } else {
            uid = user.getUid();
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        categoryFragment = CategoryFragment.newInstance();
        orderFragment = new OrderFragment();
        cartFragment = CartFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.flContent, cartFragment, "3").hide(cartFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContent, orderFragment, "2").hide(orderFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flContent, categoryFragment, "1").commit();
        activeFragment = categoryFragment;
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.cart) {
                setCurrentFragment(cartFragment);
                return true;
            } else if (item.getItemId() == R.id.category) {
                setCurrentFragment(categoryFragment);
                return true;
            } else if (item.getItemId() == R.id.order) {
                setCurrentFragment(orderFragment);
                return true;
            }
            return false;
        });
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.cart);
        badge.setBackgroundColor(getResources().getColor(R.color.design_default_color_secondary));
        firebaseFirestore.collection("cart").document(uid).addSnapshotListener((value, error) -> {
            int sum = 0;
            if (value != null && value.exists() && value.getData() != null) {
                Collection<Object> values = value.getData().values();
                for (Object o : values) {
                    sum += Math.max(Integer.parseInt(String.valueOf(o)), 0);
                }
            }
            badge.setNumber(sum);
        });
        flContent = findViewById(R.id.flContent);
    }

    public void logout(View view) {
        auth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    private void setCurrentFragment(Fragment fragment) {
        fragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
    }
}
