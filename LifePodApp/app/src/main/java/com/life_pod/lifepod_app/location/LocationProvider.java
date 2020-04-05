package com.life_pod.lifepod_app.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

public class LocationProvider {

    public interface LocationCallback {
        void onNewLocationAvailable(Location location);
    }

    public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // if network is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    callback.onNewLocationAvailable(location);
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override
                public void onProviderEnabled(String s) { }
                @Override
                public void onProviderDisabled(String s) { }
            }, null);
        /*
        use gps on phone
        this one produces more accurate results, but
        with the current config of the app it will
        yield to the top one.
         */
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onNewLocationAvailable(location);
                    }
                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) { }
                    @Override
                    public void onProviderEnabled(String s) { }
                    @Override
                    public void onProviderDisabled(String s) { }
                }, null);
            }
        }
    }
}
