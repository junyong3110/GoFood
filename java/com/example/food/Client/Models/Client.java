package com.example.food.Client.Models;

public class Client {

    private String name;
    private String password;
    private String phone;
    private String homeAddress;
    private String image;

    public Client(){

    }

    public Client(String name, String password, String phone, String homeAddress, String image) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.homeAddress = homeAddress;
        this.image = image;
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

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}