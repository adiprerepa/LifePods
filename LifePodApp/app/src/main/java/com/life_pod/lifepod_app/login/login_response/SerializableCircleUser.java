package com.life_pod.lifepod_app.login.login_response;

import java.io.Serializable;

public class SerializableCircleUser implements Serializable{

    private String username;
    private double latitude;
    private double longitude;

    public SerializableCircleUser() { }

    public SerializableCircleUser(String username, double latitude, double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
