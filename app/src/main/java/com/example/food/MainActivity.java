package com.example.food;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.food.Administrator.SignInAdministratorActivity;
import com.example.food.Client.SignInClientActivity;
import com.example.food.Restaurant.SignInRestaurantActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button clientSignIn, restaurantSignIn, administratorSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientSignIn = findViewById(R.id.btn_client_sign_in);
        clientSignIn.setOnClickListener(this);

        restaurantSignIn = findViewById(R.id.btn_restaurant_sign_in);
        restaurantSignIn.setOnClickListener(this);

        administratorSignIn = findViewById(R.id.btn_administrator_sign_in);
        administratorSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == clientSignIn) {
            Intent intent = new Intent(this, SignInClientActivity.class);
            startActivity(intent);

        } else if (view == restaurantSignIn) {
            Intent intent = new Intent(this, SignInRestaurantActivity.class);
            startActivity(intent);

        }  else if (view == administratorSignIn) {
            Intent intent = new Intent(this, SignInAdministratorActivity.class);
            startActivity(intent);
        }

    }
}