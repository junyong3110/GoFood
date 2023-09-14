package com.example.food.Restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import com.example.food.R;
import com.example.food.Restaurant.Models.UserRes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInRestaurantActivity extends AppCompatActivity {

    TextInputEditText edtPhone, edtPassword;
    MaterialButton btnSignInAsAdmin;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_restaurant);

        edtPhone =  findViewById(R.id.et_phone_number);
        edtPassword =  findViewById(R.id.et_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignInAsAdmin =  findViewById(R.id.btn_sign_in);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("UserRestaurantAdmin");

        btnSignInAsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(SignInRestaurantActivity.this, "Please enter your phone number and password", Toast.LENGTH_SHORT).show();
                }else {
                    signInRestaurant(edtPhone.getText().toString(), edtPassword.getText().toString());
                }
            }
        });
    }

    private void signInRestaurant(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignInRestaurantActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        String localPhone = phone;
        String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(localPhone).exists()) {

                    mDialog.dismiss();
                    UserRes user = dataSnapshot.child(localPhone).getValue(UserRes.class);
                    user.setPhone(localPhone);

                        if (user.getPassword().equals(localPassword)) {
                            Intent login = new Intent(SignInRestaurantActivity.this, MainAdminActivity.class);
                            ConstantRes.currentUser = user;
                            startActivity(login);
                            finish();
                        } else
                            Toast.makeText(SignInRestaurantActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();

                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignInRestaurantActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}