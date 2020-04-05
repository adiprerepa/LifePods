package database.circles.circle_entities;

import database.base_entities.BaseDatabaseEntity;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class CircleDatabaseEntity implements BaseDatabaseEntity {

    private String circleName;
    private String circleId;
    private String username;
    private String user_id;

    public CircleDatabaseEntity(String user_id, String circleName, String circleId, String username) {
        this.circleId = circleId;
        this.circleName = circleName;
        this.username = username;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCircleName() {
        return circleName;
    }

    public String getCircleId() {
        return circleId;
    }

    public String getUsername() {
        return username;
    }
}