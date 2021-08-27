package com.awn.app.mynotesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstPage extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static final String MyPrefs = "userInfo";
    String SharedPrefName, SharedPrefUserName, SharedPrefPassword;
    TextView textView;
    Button loginBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);

        loginBtn = findViewById(R.id.login);
        signUpBtn = findViewById(R.id.register);
        textView = findViewById(R.id.text);
        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        SharedPrefName =sharedPreferences.getString("name","");
        SharedPrefUserName = sharedPreferences.getString("username","");
        SharedPrefPassword = sharedPreferences.getString("password","");

        if(!SharedPrefPassword.isEmpty()&&!SharedPrefUserName.isEmpty()&&!SharedPrefName.isEmpty()){
            textView.setText("Welcome "+SharedPrefName);
            signUpBtn.setVisibility(View.INVISIBLE);
        }else{
            textView.setText("Welcome");
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterScreen.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
