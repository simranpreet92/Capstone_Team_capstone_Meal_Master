package com.capstone_team_capstone_meal_master.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Map;

public class Order implements Serializable {
    private boolean isDiscountApplied;
    private double discount;
    private double itemTotal;
    private double grandTotal;
    private int order_id;
    private Timestamp timestamp;
    private Map<String, Integer> foodItems;
    private String uid;
    private String status;
    private String paymentId;

    public Order() {
    }

    public Order(boolean isDiscountApplied, double discount, double itemTotal, double grandTotal, int order_id, Timestamp timestamp, Map<String, Integer> foodItems, String uid, String status, String paymentId) {

        this.isDiscountApplied = isDiscountApplied;
        this.discount = discount;
        this.itemTotal = itemTotal;
        this.grandTotal = grandTotal;
        this.order_id = order_id;
        this.timestamp = timestamp;
        this.foodItems = foodItems;
        this.uid = uid;
        this.status = status;
        this.paymentId = paymentId;
    }

    public boolean isDiscountApplied() {
        return isDiscountApplied;
    }

    public void setDiscountApplied(boolean discountApplied) {
        isDiscountApplied = discountApplied;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(double itemTotal) {
        this.itemTotal = itemTotal;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Integer> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(Map<String, Integer> foodItems) {
        this.foodItems = foodItems;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
