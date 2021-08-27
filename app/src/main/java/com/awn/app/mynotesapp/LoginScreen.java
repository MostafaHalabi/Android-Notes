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

public class LoginScreen extends AppCompatActivity {

    EditText username, password;
    Button loginBtn;
    SharedPreferences sharedPreferences;
    String USERNAME, PASSWORD;
    public final static String mySharedPref = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        loginBtn = findViewById(R.id.loginbtn);

        sharedPreferences = getSharedPreferences(mySharedPref, Context.MODE_PRIVATE);

        USERNAME = sharedPreferences.getString("username","");
        PASSWORD = sharedPreferences.getString("password","");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(USERNAME.isEmpty() || PASSWORD.isEmpty())
                    Toast.makeText(getApplicationContext(),"Make Sure to Sign Up First", Toast.LENGTH_LONG).show();
                else if(!USERNAME.equals(username.getText().toString()) || !PASSWORD.equals(password.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Wrong Credentials", Toast.LENGTH_LONG).show();
                }
                else if(USERNAME.equals(username.getText().toString()) && PASSWORD.equals(password.getText().toString())){
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
