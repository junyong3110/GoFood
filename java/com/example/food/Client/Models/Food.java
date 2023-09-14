package com.example.food.Client.Models;


public class Food {

    private String name,image,description,price,menuId;
    private String restaurantId;

    public Food(){

    }

    public Food(String name, String image, String description, String price, String menuId, String RestaurantId) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
        this.restaurantId = RestaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
