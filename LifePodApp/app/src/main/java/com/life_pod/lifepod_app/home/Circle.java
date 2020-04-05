package com.life_pod.lifepod_app.home;

import java.util.ArrayList;

public class Circle {

    private String circleName;
    private ArrayList<PodData> podData;

    public Circle() { }

    public Circle(String circleName, ArrayList<PodData> podData) {
        this.circleName = circleName;
        this.podData = podData;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public ArrayList<PodData> getPodData() {
        return podData;
    }

    public void setPodData(ArrayList<PodData> podData) {
        this.podData = podData;
    }
}
