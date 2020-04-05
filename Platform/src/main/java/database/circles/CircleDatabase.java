package database.circles;

import database.BaseDatabase;
import database.circles.circle_entities.CircleDatabaseEntity;
import database.circles.circle_entities.CircleDatabaseIdentifier;
import database.circles.circle_entities.CircleEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class CircleDatabase extends BaseDatabase<CircleDatabaseEntity, CircleDatabaseIdentifier<String>> {

    private String tablename;

    public CircleDatabase() { }

    public CircleDatabase(String url, String username, String password, String tablename) {
        super(url, username, password);
        this.tablename = tablename;
    }


    // when creating a circle - exception thrown if something bad happens
    @Override
    public void insert(CircleDatabaseEntity entity) throws SQLException {
        Statement statement = super.connection.createStatement();
        String insertionStatement = String.format("insert into %s (username, user_id, circle_id, circle_name) values (%s, %s, %s, %s);", tablename,
                entity.getUsername(), entity.getUser_id(), entity.getCircleId(), entity.getCircleName());
        statement.execute(insertionStatement);
    }

    //retrieve circles associated with username
    // identifier is circleid
    @Override
    public ArrayList<CircleDatabaseEntity> retrieveEntity(CircleDatabaseIdentifier<String> circleDatabaseIdentifier) throws SQLException {
        ArrayList<CircleDatabaseEntity> entities = new ArrayList<>();
        Statement statement = super.connection.createStatement();
        // alternamename is the username keyword in db
        String retrievalStatement = String.format("select * from %s where `%s` = %s;", tablename, circleDatabaseIdentifier.getAlternameName(), circleDatabaseIdentifier.getIdentifier());
        ResultSet resultSet = statement.executeQuery(retrievalStatement);
        // add entities
        while (resultSet.next()) {
            entities.add(
                    new CircleDatabaseEntity(
                            resultSet.getString("user_id"),
                            resultSet.getString("circle_name"),
                            resultSet.getString("circle_id"),
                            resultSet.getString("username")
                    )
            );
        }
        return entities;
    }

    // todo check in call if returned list has size 0
    public ArrayList<CircleEntity> retrieveCircleList(CircleDatabaseIdentifier<String> username) throws SQLException {
        Statement statement = super.connection.createStatement();
        ArrayList<String> circleIds = new ArrayList<>();
        // get all circleIds associated with username
        String circleListRetrievalStatement = String.format("select * from %s where `%s` = '%s';", tablename, username.getAlternameName(), username.getIdentifier());
        ResultSet resultSet = statement.executeQuery(circleListRetrievalStatement);
        // pof
        while (resultSet.next()) {
            circleIds.add(resultSet.getString("circle_id"));
        }
        // circles corresponding to user - to be returned
        ArrayList<CircleEntity> circles = new ArrayList<>();
        // for each circle ID the user has, retrieve users
        for (String circleId : circleIds) {
            CircleEntity circleEntity = new CircleEntity();
            String usersRetrievalCommand = String.format("select * from %s where `%s` = '%s';", tablename, username.getName(), circleId);
            Statement usersRetrievalStatement = super.connection.createStatement();
            ResultSet usersResultSet = usersRetrievalStatement.executeQuery(usersRetrievalCommand);
            while (usersResultSet.next()) {
                circleEntity.setCircleName(usersResultSet.getString("circle_name"));
                circleEntity.addCircleUser(usersResultSet.getString("username"));
            }
            circles.add(circleEntity);
        }
        return circles;
    }
    // identifier has to be circleId
    @Override
    public boolean authenticate(CircleDatabaseIdentifier<String> circleDatabaseIdentifier) throws SQLException {
        Statement statement = super.connection.createStatement();
        // for determining if the user has the right circle id
        // select * from table where circle_id = xxx
        String authenticateStatement = String.format("select * from %s where `%s` = '%s';", tablename, circleDatabaseIdentifier.getName(), circleDatabaseIdentifier.getIdentifier());
        System.out.println(authenticateStatement);
        ResultSet resultSet = statement.executeQuery(authenticateStatement);
        // if there was a result - meaning auth success
        return resultSet.next();
    }

    public ArrayList<String> getCircleIds(String user_id) throws SQLException {
        Statement statement = super.connection.createStatement();
        ArrayList<String> circleIds = new ArrayList<>();
        // get all circleIds associated with username
        String circleListRetrievalStatement = String.format("select * from %s where `%s` = '%s';", tablename, "user_id", user_id);
        ResultSet resultSet = statement.executeQuery(circleListRetrievalStatement);
        // pof
        while (resultSet.next()) {
            circleIds.add(resultSet.getString("circle_id"));
        }
        return circleIds;
    }
}
