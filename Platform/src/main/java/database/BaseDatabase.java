package database;

import database.base_entities.BaseDatabaseEntity;
import database.base_entities.BaseDatabaseIdentifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public abstract class BaseDatabase<DatabaseEntity extends BaseDatabaseEntity, EntityIdentifier extends BaseDatabaseIdentifier> {

    public Connection connection;

    // no one can instantiate
    protected BaseDatabase() { }

    public BaseDatabase(String databaseUrl, String databaseUsername, String databasePassword) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to mySQL database...");
            connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
            System.out.println("Connected to mySQL database successfully!");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.err.println("An error happened with sql");
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Unable to find class");
        }
    }

    public abstract void insert(DatabaseEntity entity) throws SQLException;

    public abstract ArrayList<DatabaseEntity> retrieveEntity(EntityIdentifier entityIdentifier) throws SQLException, Exception;

    public abstract boolean authenticate(EntityIdentifier entityIdentifier) throws SQLException;
}
