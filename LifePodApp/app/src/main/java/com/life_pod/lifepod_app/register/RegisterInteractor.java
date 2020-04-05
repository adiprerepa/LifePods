package com.life_pod.lifepod_app.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.life_pod.lifepod_app.login.LoginActivity;

import pod.EventInterface;

public class RegisterInteractor {

    boolean registerCredentialsValid(EditText username, EditText password, EditText email, EditText phoneNumber) {
        String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
        String phoneRegex = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$\n";
//        return !username.toString().equals("") && !password.toString().equals("") && email.toString().matches(emailRegex) && phoneNumber.toString().matches(phoneRegex);
        return !username.toString().equals("") && !password.toString().equals("");
    }

    void displayErrorToast(String message, Context ctx) {
        Toast t = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        t.show();
    }

    public void handleRegisterResponse(EventInterface.SignUpResponse response, Activity activity) {
        // handle registration by redirecting to login
        if (response.getStatus()) {
            activity.startActivity(new Intent(activity.getApplicationContext(), LoginActivity.class));
        } else {
            displayErrorToast("registration failed", activity.getApplicationContext());
        }
    }

}
