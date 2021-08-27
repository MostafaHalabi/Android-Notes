package com.awn.app.mynotesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterScreen extends AppCompatActivity {


    EditText name, username, password;
    Button signupBtn;

    SharedPreferences sharedPreferences;

    String Name, UserName, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);

        signupBtn = findViewById(R.id.signupbtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser(){
        Name = name.getText().toString().trim();
        UserName = username.getText().toString().trim();
        Password = password.getText().toString().trim();

        if(Name.isEmpty())
        {
            name.setError("First Name is required");
            name.requestFocus();
            return;
        }

        if(UserName.isEmpty())
        {
            username.setError("Last Name is required");
            username.requestFocus();
            return;
        }

        if(Password.isEmpty())
        {
            password.setError("userEmail is required");
            password.requestFocus();
            return;
        }

        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",Name);
        editor.putString("username",UserName);
        editor.putString("password",Password);
        editor.apply();

        Toast.makeText(getApplicationContext(), "Sign up Successful !", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), FirstPage.class);
        startActivity(intent);
        finish();


    }
}
