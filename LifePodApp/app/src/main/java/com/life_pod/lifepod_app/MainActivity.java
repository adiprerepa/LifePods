package com.life_pod.lifepod_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.life_pod.lifepod_app.login.LoginActivity;
import com.life_pod.lifepod_app.register.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button loginButton = findViewById(R.id.loginButtonWlcm);
        Button registerButton = findViewById(R.id.registerButtonWlcm);
        loginButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        registerButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }
}
