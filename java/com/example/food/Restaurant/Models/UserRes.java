package com.example.food.Restaurant.Models;

public class UserRes {

    private String name;
    private String password;
    private String phone;
    private String restaurantId;

    public UserRes(){
    }

    public UserRes(String name, String password, String phone, String restaurantId) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
