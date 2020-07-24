package com.example.giveapp;

import java.io.Serializable;

public class Food implements Serializable{
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodQty() {
        return foodQty;
    }

    public void setFoodQty(String foodQty) {
        this.foodQty = foodQty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodLogo() {
        return foodLogo;
    }

    public void setFoodLogo(String foodLogo) {
        this.foodLogo = foodLogo;
    }

    private String foodName;

    public Food() {
    }

    public Food(String foodName, String foodDesc, String foodPrice, String foodQty, String id, String foodLogo) {
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.foodPrice = foodPrice;
        this.foodQty = foodQty;
        this.id = id;
        this.foodLogo = foodLogo;
    }

    private String foodDesc;
    private String foodPrice;
    private String foodQty;
    private String id;
    private String foodLogo;


}
