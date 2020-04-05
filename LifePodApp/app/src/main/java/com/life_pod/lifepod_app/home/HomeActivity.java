package com.life_pod.lifepod_app.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.api.NotificationTopicsTask;
import com.life_pod.lifepod_app.bluetooth.BluetoothService;
import com.life_pod.lifepod_app.bluetooth.DeviceStatusActivity;
import com.life_pod.lifepod_app.bluetooth.EventStore;
import com.life_pod.lifepod_app.circle_switch.CircleSwitcherActivity;
import com.life_pod.lifepod_app.login.LoginActivity;
import com.life_pod.lifepod_app.login.login_response.SerializableResponse;
import com.life_pod.lifepod_app.notification.UpdateFirebaseTokenTask;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import pod.EventInterface;

public class HomeActivity extends AppCompatActivity {

    public static String LOG_TAG = "HomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_userlist);
//        EventStore.init();
        // bluetooth
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);

        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("LifePodDevice")) {
                    Log.e("Device", device.getName());
                    Intent intent = new Intent(HomeActivity.this, BluetoothService.class);
                    intent.putExtra("uuid", UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
                    intent.putExtra("bonded_device", device);
                    startService(intent);
                    break;
                }
            }
        }

        // firebase
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(LOG_TAG, "getInstanceId failed", task.getException());
                return;
            }
            new UpdateFirebaseTokenTask()
                    .execute(task.getResult().getToken());
        });
        // requires no params :)
        new NotificationTopicsTask()
                .execute();

        // logout
        Button logoutButton = findViewById(R.id.butt_logout);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
            preferencesEditor.putString("username", null);
            preferencesEditor.putString("password", null);
            preferencesEditor.commit();
            preferencesEditor.apply();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        TextView circle_name = findViewById(R.id.circleName_userlist);
        Button device_status = findViewById(R.id.device_status);
        HomeInteractor homeInteractor = new HomeInteractor();
        SerializableResponse response = (SerializableResponse) getIntent().getSerializableExtra("LoginResponse");
        assert response != null : Log.v(LOG_TAG, "You done fucked up bruh");
        EventInterface.LoginResponse loginResponse = homeInteractor.getLoginResponseFromSerializableMessage(response);
        if (loginResponse.isInitialized()) {
            String defaultCircle = getIntent().getStringExtra("circleName");
            circle_name.setText(defaultCircle);
            RecyclerView recyclerView = findViewById(R.id.podRecyclerView);
            ArrayList<Circle> recievedCircles = homeInteractor.getPodData(loginResponse);
            ArrayList<PodData> curPods = new ArrayList<>();
            for (Circle c : recievedCircles) {
                if (c.getCircleName().equals(defaultCircle)) {
                    curPods = c.getPodData();
                }
            }
            PodListAdapter podListAdapter = new PodListAdapter(curPods);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(podListAdapter);
            Button button = findViewById(R.id.circleSwitchButt);
            final ArrayList<String> circleNames = homeInteractor.getCircleNames(loginResponse);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(HomeActivity.this, CircleSwitcherActivity.class);
                intent.putStringArrayListExtra("circleNames", circleNames);
                startActivity(intent);
            });
        } else {
            Log.v(LOG_TAG, "Not Initialized LoginResponse! :) Now go kill yourself!");
        }
        device_status.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DeviceStatusActivity.class);
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("LifePodDevice")) {
                    intent.putExtra("bonded_devices", device);
                }
            }
            startActivity(intent);
        });
    }
}