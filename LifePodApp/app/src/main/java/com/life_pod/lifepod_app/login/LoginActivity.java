package com.life_pod.lifepod_app.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.api.LoginTask;
import com.life_pod.lifepod_app.bluetooth.EventStore;
import com.life_pod.lifepod_app.entities.LoginAttemptEntity;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {

    private LoginInteractor loginInteractor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventStore.init();
        loginInteractor = new LoginInteractor();
        Activity activity = this;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        String username_sp = sharedPreferences.getString("username", null);
        String password_sp = sharedPreferences.getString("password", null);
        // if both exist
        if (!(username_sp == null || password_sp == null)) {
            new LoginTask(activity).execute(new LoginAttemptEntity(username_sp, password_sp));
        } else {
            setContentView(R.layout.activity_login);
            Button loginButton = findViewById(R.id.login_butt);
            loginButton.setOnClickListener(view -> {
                if (loginInteractor.assertEntitiesNotNull(findViewById(R.id.usernameEntry_login), findViewById(R.id.passwordEntry_login))) {
                    String username = ((EditText) findViewById(R.id.usernameEntry_login)).getText().toString();
                    String password = ((EditText) findViewById(R.id.passwordEntry_login)).getText().toString();
                    new LoginTask(activity).execute(new LoginAttemptEntity(username, password));
                } else {
                    loginInteractor.displayErrorToast("Fill Both Fields out.", getApplicationContext());
                }
            });
        }
    }
}