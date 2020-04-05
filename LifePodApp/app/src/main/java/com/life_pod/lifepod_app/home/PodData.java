package com.life_pod.lifepod_app.home;

public class PodData {

    // maybe add event data here?
    private String username;
    private String lastLocation;

    public PodData(String username, String lastLocation) {
        this.username = username;
        this.lastLocation = lastLocation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }
}
