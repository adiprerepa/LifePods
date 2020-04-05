package database.authentication.entities;

import database.base_entities.BaseDatabaseEntity;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class AuthenticationDatabaseEntity implements BaseDatabaseEntity {

    private String username;
    private String password;
    private String userId;
    private String email;
    private String phone_number;

    public AuthenticationDatabaseEntity(String username, String password, String userId, String email, String phone_number) {
        this.email = email;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone_number = phone_number;
    }


    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
