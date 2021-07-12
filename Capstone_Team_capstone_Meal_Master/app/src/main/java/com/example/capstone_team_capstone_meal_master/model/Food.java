package com.example.capstone_team_capstone_meal_master.model;

public class Food {
    private String id, name, url;
    private double price;
    private Category category;

    public Food(String id, String name, String url, double price) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.price = price;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


}
