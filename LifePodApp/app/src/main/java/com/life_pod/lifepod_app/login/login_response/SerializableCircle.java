package com.life_pod.lifepod_app.login.login_response;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableCircle implements Serializable {

    private ArrayList<SerializableCircleUser> users;
    private String circleName;

    public SerializableCircle() { }

    public SerializableCircle(ArrayList<SerializableCircleUser> users, String circleName) {
        this.users = users;
        this.circleName = circleName;
    }

    public ArrayList<SerializableCircleUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<SerializableCircleUser> users) {
        this.users = users;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
}
