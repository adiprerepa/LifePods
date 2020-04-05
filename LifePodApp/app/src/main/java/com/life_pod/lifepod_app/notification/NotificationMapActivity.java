package com.life_pod.lifepod_app.notification;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.life_pod.lifepod_app.R;

import java.util.Objects;

public class NotificationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private String targetPersonUsername = "";
    private LatLng targetLocation;
    private int threatPriority;

    /**
     * Only Works if onCreate() is called before onMapReady.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        targetPersonUsername = LifePodMessagingService.Constants.MapsData.username;
        String latitude = LifePodMessagingService.Constants.MapsData.latitude;
        String longitude = LifePodMessagingService.Constants.MapsData.longitude;
        targetLocation = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        threatPriority = Integer.valueOf(LifePodMessagingService.Constants.MapsData.priority);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // make sure values were passed to intent and set.
        if (targetLocation.latitude != -1.00 && targetLocation.longitude != -1.00 && threatPriority != -1 && !targetPersonUsername.equals("")) {
           gMap.addMarker(new MarkerOptions().position(targetLocation).title(targetPersonUsername));
           gMap.moveCamera(CameraUpdateFactory.newLatLng(targetLocation));
           gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 12.0f));
        }
    }
}
