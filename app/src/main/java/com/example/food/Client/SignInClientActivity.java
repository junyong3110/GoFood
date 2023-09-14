package com.example.food.Client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Administrator.AddRestaurantsActivity;
import com.example.food.Administrator.AddRestaurantsAdmin;
import com.example.food.Administrator.ConstantAdmin;
import com.example.food.Administrator.SignInAdministratorActivity;
import com.example.food.Client.Models.Client;
import com.example.food.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInClientActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText edtPhone, edtPassword;
    MaterialButton btnSignIn;
    TextView txtSignUp;
    FirebaseDatabase db;
    DatabaseReference client;
    String phoneNumber;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_client);

        edtPhone = findViewById(R.id.et_phone_number);
        edtPassword = findViewById(R.id.et_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        txtSignUp = findViewById(R.id.txt_sign_up);
        txtSignUp.setOnClickListener(this);

        db = FirebaseDatabase.getInstance();
        client = db.getReference("Client");

    }

    @Override
    public void onClick(View view) {

        if (view == btnSignIn) {

            if(edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                Toast.makeText(SignInClientActivity.this, "Please enter your phone number and password", Toast.LENGTH_SHORT).show();
            }else {
                buttonSignIn();
            }

        } else if (view == txtSignUp) {
            Intent intent = new Intent(this, SignUpClientActivity.class);
            startActivity(intent);
        }

    }

    private void buttonSignIn() {

        final ProgressDialog mDialog = new ProgressDialog(SignInClientActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        phoneNumber = edtPhone.getText().toString();
        password = edtPassword.getText().toString();

        client.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(phoneNumber).exists()) {

                    mDialog.dismiss();
                    Client client = dataSnapshot.child(phoneNumber).getValue(Client.class);
                    client.setPhone(phoneNumber);

                    if (client.getPassword().equals(password)) {
                        Toast.makeText(SignInClientActivity.this, "Sign In Successfully !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignInClientActivity.this, RestaurantListActivity.class);
                        Constant.currentUser = client;
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInClientActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignInClientActivity.this, "User not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}