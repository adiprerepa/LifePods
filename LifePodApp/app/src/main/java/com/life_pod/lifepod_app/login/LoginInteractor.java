package com.life_pod.lifepod_app.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.life_pod.lifepod_app.bluetooth.EventStore;
import com.life_pod.lifepod_app.entities.LoginAttemptEntity;
import com.life_pod.lifepod_app.home.HomeActivity;
import com.life_pod.lifepod_app.login.login_response.SerializableCircle;
import com.life_pod.lifepod_app.login.login_response.SerializableCircleUser;
import com.life_pod.lifepod_app.login.login_response.SerializableResponse;

import java.util.ArrayList;

import pod.EventInterface;

public class LoginInteractor {

    private static String TAG = "LoginInteractor";

    boolean assertEntitiesNotNull(EditText text1, EditText text2) {
        return (!text1.getText().toString().equals(""))
                || (!text2.getText().toString().equals(""));
    }

    void displayErrorToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void handleLoginResponse(EventInterface.LoginResponse response, Activity activity, LoginAttemptEntity attemptEntity) {
        SerializableResponse serializableResponse = getSerializableResponseFromProtobufResponse(response);
        EventStore.user_id = response.getUserId();
        if (serializableResponse.isStatus()) {
            Intent intent = new Intent(activity.getApplicationContext(), HomeActivity.class);
            intent.putExtra("LoginResponse", serializableResponse);
            intent.putExtra("circleName", serializableResponse.getCircles().get(0).getCircleName());
            Log.d(TAG, "handleLoginResponse: Launching into home");
            SharedPreferences sharedPreferences = activity.getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString("username", attemptEntity.getUsername());
            sharedPreferencesEditor.putString("password", attemptEntity.getPassword());
            sharedPreferencesEditor.apply();
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
            displayErrorToast("Username Or Password is Wrong", activity.getApplicationContext());
            activity.startActivity(intent);
        }
    }

    private SerializableResponse getSerializableResponseFromProtobufResponse(EventInterface.LoginResponse response) {
        ArrayList<SerializableCircle> serializableCircles = new ArrayList<>();
        for (EventInterface.Circle c : response.getResponseCirclesList()) {
            ArrayList<SerializableCircleUser> serializableCircleUsers = new ArrayList<>();
            for (EventInterface.CircleUser user : c.getCircleUsersList()) {
                serializableCircleUsers.add(new SerializableCircleUser(user.getUsername(), user.getLastCoordinates().getLatitude(), user.getLastCoordinates().getLongitude()));
            }
            serializableCircles.add(new SerializableCircle(serializableCircleUsers, c.getCircleName()));
        }
        return new SerializableResponse(serializableCircles, response.getStatus());
    }
}