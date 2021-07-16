package com.capstone_team_capstone_meal_master.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Food, Integer> cartItems;

    public Cart() {
        this.cartItems = new HashMap<>();
    }

    public Map<Food, Integer> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<Food, Integer> cartItems) {
        this.cartItems = cartItems;
    }

}
