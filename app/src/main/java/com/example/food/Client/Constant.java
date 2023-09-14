package com.example.food.Client;

import com.example.food.Client.Models.Client;

import java.util.Calendar;
import java.util.Locale;

public class Constant {
    public static Client currentUser;
    public static final String CATEGORY_ID = "CategoryId";
    public static final String FOOD_ID = "FoodId";
    public static final String RESTAURANT_ID = "RestaurantId";
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static  String restaurantSelected = "";

    public static String currentKey;

    public static String PHONE_TEXT = "userPhone";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static String convertCodeToStatus(String status) {

        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "Preparing Orders";
        else if(status.equals("2"))
            return "Shipping";
        else
            return "Delivered";
    }

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd-MM-yyyy hh:mm aaa"
                , calendar).toString());
        return date.toString();
    }


}
