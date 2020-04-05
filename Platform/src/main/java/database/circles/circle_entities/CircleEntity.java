package database.circles.circle_entities;

import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class CircleEntity {

    private ArrayList<String> users = new ArrayList<>();
    private String circleName;

    public CircleEntity() { }

    public CircleEntity(String circleName) {
        this.circleName = circleName;
    }

    public CircleEntity(ArrayList<String> users, String circleName) {
        this(circleName);
        this.users = users;
    }

    public void addCircleUser(String username) {
        users.add(username);
    }

    public void setUsers(ArrayList<String> circleUsers) {
        users.addAll(circleUsers);
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String name) {
        this.circleName = circleName;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
