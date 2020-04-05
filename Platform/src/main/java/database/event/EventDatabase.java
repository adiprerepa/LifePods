package database.event;

import database.BaseDatabase;
import database.event.event_entities.EventDatabaseEntity;
import database.event.event_entities.EventDatabaseIdentifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class EventDatabase extends BaseDatabase<EventDatabaseEntity, EventDatabaseIdentifier<String>> {

    private String tableName;

    public EventDatabase(String url, String username, String password, String tableName) {
        super(url, username, password);
        this.tableName = tableName;
    }

    @Override
    public void insert(EventDatabaseEntity entity) throws SQLException {
        String insertionCommand = String.format("insert into %s (user_id, threat_priority, time_of_incident, latitude, longitude) values ('%s', '%s', '%s', '%s', '%s');", tableName,
                entity.getUserId(), entity.getThreatPriority(), Instant.now().toString(), entity.getCoordinates().getLatitude(), entity.getCoordinates().getLongitude());
        System.out.println(insertionCommand);
        Statement statement = super.connection.createStatement();
        statement.execute(insertionCommand);
    }

    // identifier is user_id
    @Override
    public ArrayList<EventDatabaseEntity> retrieveEntity(EventDatabaseIdentifier<String> eventDatabaseIdentifier) throws Exception {
        ArrayList<EventDatabaseEntity> retrievalEntities = new ArrayList<>();
        String retrievalStatement = String.format("select * from %s where `%s` = %s;", tableName, eventDatabaseIdentifier.getName(), eventDatabaseIdentifier.getIdentifier());
        Statement statement = super.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(retrievalStatement);
        while (resultSet.next()) {
            retrievalEntities.add(
                    new EventDatabaseEntity(eventDatabaseIdentifier.getIdentifier(), Instant.parse(resultSet.getString("time_of_incident")), Integer.parseInt(resultSet.getString("threat_priority")),
                            Double.parseDouble(resultSet.getString("latitiude")), Double.parseDouble(resultSet.getString("longitude"))));
        }
        return retrievalEntities;
    }

    @Override
    public boolean authenticate(EventDatabaseIdentifier<String> stringEventDatabaseIdentifier) {
        return false;
        // not implemented
    }
}
