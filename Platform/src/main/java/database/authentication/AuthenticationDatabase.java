package database.authentication;

import database.BaseDatabase;
import database.authentication.entities.AuthenticationDatabaseEntity;
import database.authentication.entities.AuthenticationDatabaseIdentifier;
import jdk.jshell.spi.ExecutionControl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class AuthenticationDatabase extends BaseDatabase<AuthenticationDatabaseEntity, AuthenticationDatabaseIdentifier<String>> {

    private String tableName;
    private String tmpPassword;
    private String tmpUserId;

    // for testing
    public AuthenticationDatabase() { }

    public AuthenticationDatabase(String url, String username, String password, String tableName) {
        super(url, username, password);
        this.tableName = tableName;
    }

    // registration
    @Override
    public void insert(AuthenticationDatabaseEntity entity) throws SQLException {
        Statement stmt = super.connection.createStatement();
        String insertStatement = String.format("insert into %s (username, user_id, email, phone_number, password) values ('%s', '%s', '%s', '%s', '%s');",
                tableName,
                entity.getUsername(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getPhone_number(),
                entity.getPassword());
        stmt.execute(insertStatement);
        // will throw exception if user already found
    }

    // done by circle db
    @Override
    public ArrayList<AuthenticationDatabaseEntity> retrieveEntity(AuthenticationDatabaseIdentifier<String> authenticationDatabaseIdentifier) throws Exception {
        throw new ExecutionControl.NotImplementedException("Haha u thot");
    }

    public void insertRegistrationToken(String user_id, String reg_token) throws SQLException {
        // cell = token_id
        // identifier = user_id
        Statement statement = super.connection.createStatement();
        String insertStatement = String.format("update %s set token_id = '%s' where user_id = '%s';", tableName, reg_token, user_id);
        statement.execute(insertStatement);
    }

    // login
    @Override
    public boolean authenticate(AuthenticationDatabaseIdentifier<String> authenticationDatabaseIdentifier) throws SQLException {
        boolean authStatus = false;
        Statement statement = super.connection.createStatement();
        String authenticationStatement = String.format("select * from %s where `%s` = '%s';", tableName, authenticationDatabaseIdentifier.getName(), authenticationDatabaseIdentifier.getIdentifier());
        System.out.println(authenticationStatement);
        ResultSet resultSet = statement.executeQuery(authenticationStatement);
        while (resultSet.next()) {
            if (tmpPassword.equals(resultSet.getString("password"))) {
                tmpUserId = resultSet.getString("user_id");
                authStatus = true;
            }
        }
         return authStatus;
    }

    /**
     * Returns null if user_id does not exist
     * @param user_id id
     */
    public String lookupUsername(String user_id) throws SQLException {
        String username = null;
        String lookup = String.format("select * from %s where `%s` = '%s';", tableName, "user_id", user_id);
        Statement statement = super.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(lookup);
        while (resultSet.next()) {
            username = resultSet.getString("username");
        }
        return username;
    }

    public void injectPassword(String tmpPassword) {
        this.tmpPassword = tmpPassword;
    }

    public String getUserId() {
        System.out.printf("User ID : %s\n", tmpUserId);
        return tmpUserId;
    }
}