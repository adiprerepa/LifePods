package database.event.event_entities;

import database.base_entities.BaseDatabaseEntity;

import java.time.Instant;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class EventDatabaseEntity implements BaseDatabaseEntity {

    private String userId;
    private Instant timeOfOccurance;
    private int threatPriority;
    private GPSCoordinates coordinates;

    public EventDatabaseEntity() { }

    public EventDatabaseEntity(String userId, Instant timeOfOccurance, int threatPriority, double latitude, double longitude) {
        this.userId = userId;
        this.timeOfOccurance = timeOfOccurance;
        this.threatPriority = threatPriority;
        this.coordinates = new GPSCoordinates(latitude, longitude);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getTimeOfOccurance() {
        return timeOfOccurance;
    }

    public void setTimeOfOccurance(Instant timeOfOccurance) {
        this.timeOfOccurance = timeOfOccurance;
    }

    public int getThreatPriority() {
        return threatPriority;
    }

    public void setThreatPriority(int threatPriority) {
        this.threatPriority = threatPriority;
    }

    public GPSCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GPSCoordinates coordinates) {
        this.coordinates = coordinates;
    }
}