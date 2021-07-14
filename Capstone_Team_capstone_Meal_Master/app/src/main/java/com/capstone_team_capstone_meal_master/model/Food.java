package com.capstone_team_capstone_meal_master.model;

import java.io.Serializable;

public class Food {
    private String id, name, url, category;
    private double price;
   // private Category category;



    public Food(String category, String id, String name, String url, double price) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
