package com.life_pod.lifepod_app.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.api.RegisterTask;
import com.life_pod.lifepod_app.entities.RegisterAttemptEntity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RegisterInteractor registerInteractor = new RegisterInteractor();
        setContentView(R.layout.activity_register);
        Button button = findViewById(R.id.registerButt);
        final Activity activity = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerInteractor.registerCredentialsValid(
                        (EditText) findViewById(R.id.userName_reg),
                        (EditText) findViewById(R.id.passwordEntry_reg),
                        (EditText) findViewById(R.id.emailEntry_reg),
                        (EditText) findViewById(R.id.phoneEntry_reg)
                )) {
                    // creds valid
                    // grpc
                    new RegisterTask(activity).execute(new RegisterAttemptEntity(
                            ((EditText) findViewById(R.id.userName_reg)).getText().toString(),
                            ((EditText) findViewById(R.id.passwordEntry_reg)).getText().toString(),
                            ((EditText) findViewById(R.id.emailEntry_reg)).getText().toString(),
                            ((EditText) findViewById(R.id.phoneEntry_reg)).getText().toString()));
                } else {
                    // not valid, toast
                    registerInteractor.displayErrorToast("Please Fill out all fields properly", getApplicationContext());
                }
            }
        });
    }
}
