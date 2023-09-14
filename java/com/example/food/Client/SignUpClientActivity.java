package com.example.food.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Client.Models.Client;
import com.example.food.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpClientActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText edtPhone, edtName, edtPassword;
    MaterialButton btnSignUp;
    TextView txtSignIn;
    FirebaseDatabase db;
    DatabaseReference client;

    String phoneNumber;
    String name;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_client);

        edtPhone = findViewById(R.id.et_phone_number);
        edtName = findViewById(R.id.et_name);
        edtPassword = findViewById(R.id.et_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPassword.setTransformationMethod(new PasswordTransformationMethod());

        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(this);
        txtSignIn = findViewById(R.id.txt_sign_in);
        txtSignIn.setOnClickListener(this);

        db = FirebaseDatabase.getInstance();
        client = db.getReference("Client");

    }

    @Override
    public void onClick(View view) {

        if (view == btnSignUp) {

            buttonSignUp();

        } else if (view == txtSignIn) {
            Intent intent = new Intent(SignUpClientActivity.this, SignInClientActivity.class);
            startActivity(intent);
        }

    }

    public void buttonSignUp() {

        if (!validatePhoneNumber() | !validateName() | !validatePassword() ) {
            Toast.makeText(this, "Please enter correct info ", Toast.LENGTH_SHORT).show();

        } else {

                final ProgressDialog mDialog = new ProgressDialog(SignUpClientActivity.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                phoneNumber = edtPhone.getText().toString();
                name = edtName.getText().toString();
                password = edtPassword.getText().toString();

                client.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //check if user not exit in database
                        if (dataSnapshot.child(phoneNumber).exists()) {
                            //Get User Information
                            mDialog.dismiss();
                            Toast.makeText(SignUpClientActivity.this, "Phone Number already exists", Toast.LENGTH_SHORT).show();

                        } else {
                            mDialog.dismiss();
                            Client clientUsers = new Client(name, password, phoneNumber, null, null);
                            client.child(phoneNumber).setValue(clientUsers);
                            Toast.makeText(SignUpClientActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
    }

    private boolean validatePhoneNumber() {
        phoneNumber = edtPhone.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(SignUpClientActivity.this, "Phone Number is required. Can't be empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNumber.length() < 9) {
            Toast.makeText(SignUpClientActivity.this, "Phone Number cannot less than 10 digits!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNumber.length() > 13) {
            Toast.makeText(SignUpClientActivity.this, "Phone Number cannot exceed 13 digits!", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private boolean validateName() {
        name = edtName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(SignUpClientActivity.this, "UserName is required. Can't be empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private boolean validatePassword() {
        password = edtPassword.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(SignUpClientActivity.this, "Password is required. Can't be empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

}