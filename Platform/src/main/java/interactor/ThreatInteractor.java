package interactor;

import com.google.firebase.messaging.*;
import database.authentication.AuthenticationDatabase;
import database.circles.CircleDatabase;
import database.event.EventDatabase;
import database.event.event_entities.EventDatabaseEntity;
import database.event.event_entities.EventDatabaseIdentifier;
import pod.EventInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class ThreatInteractor {

    private EventDatabase eventDatabase;
    private AuthenticationDatabase authenticationDatabase;
    private CircleDatabase circleDatabase;

    public ThreatInteractor(EventDatabase eventDatabase, AuthenticationDatabase authenticationDatabase, CircleDatabase circleDatabase) {
        this.eventDatabase = eventDatabase;
        this.authenticationDatabase = authenticationDatabase;
        this.circleDatabase = circleDatabase;
    }

    /*
    todo Handle alerting of 3rd party entities (i.e. police) here.
     */
    public EventInterface.ThreatPriorityResponse handleThreatPriorityUpdate(EventInterface.ThreatEvent priority) {
        EventInterface.ThreatPriorityResponse response;
        try {
            eventDatabase.insert(new EventDatabaseEntity(priority.getUserId(), Instant.now(), priority.getThreatPriority(), priority.getCoordinates().getLatitude(), priority.getCoordinates().getLongitude()));
            response = EventInterface.ThreatPriorityResponse.newBuilder().setStatus(true).build();
            System.out.println("Handled Threat Priority Update Successfully!");
        } catch (SQLException e) {
            response = EventInterface.ThreatPriorityResponse.newBuilder().setStatus(false).build();
            e.printStackTrace();
            System.out.println("Something went wrong...");
        }
        return response;

    }

    public void sendPush(EventInterface.ThreatEvent entity) throws SQLException, FirebaseMessagingException {
        ArrayList<String> circleNames = getCircleNamesAssociatedWithUserId(entity.getUserId());
        for (int i = 0; i < circleNames.size(); i++) {
            circleNames.set(i, circleNames.get(i).concat("_fcmtopic"));
        }
        // TODO: 1/2/20 Set notificaiton in push
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("latitude", String.valueOf(entity.getCoordinates().getLatitude()));
        notificationData.put("longitude", String.valueOf(entity.getCoordinates().getLongitude()));
        String username = authenticationDatabase.lookupUsername(entity.getUserId());
        if (username == null){
            username = "Unknown User";
        }
        notificationData.put("username", username);
        notificationData.put("threat_priority", String.valueOf(entity.getThreatPriority()));

        // send notification to each topic
        for (String circleName : circleNames) {
            System.out.println(circleName);
            Message message = Message.builder().putAllData(notificationData)
                    .setWebpushConfig(WebpushConfig.builder().putHeader("Urgency", "high").build())
                    .setTopic(circleName)
                    .putData("priority", "10")
                    .build();
            System.out.printf("Sending push notification to topic: %s\n", circleName);
            System.out.printf("Notification Body: %s\n", message.toString());
            FirebaseMessaging.getInstance().send(message);
        }
        System.out.println("Sent Push!");
    }

    public ArrayList<EventInterface.ThreatEvent> getAllEvents(String userId) {
        try {
            ArrayList<EventDatabaseEntity> entities = eventDatabase.retrieveEntity(new EventDatabaseIdentifier<>(userId));
            ArrayList<EventInterface.ThreatEvent> eventEntities = new ArrayList<>();
            entities.forEach(entity -> eventEntities.add(
                    EventInterface.ThreatEvent.newBuilder()
                            .setCoordinates(
                                    EventInterface.GpsCoords.newBuilder()
                                            .setLatitude(entity.getCoordinates().getLatitude())
                                            .setLongitude(entity.getCoordinates().getLongitude())
                                            .build())
                            .setUserId(entity.getUserId())
                            .setThreatPriority(entity.getThreatPriority())
                            .build()));
            return eventEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<String> getCircleNamesAssociatedWithUserId(String user_id) {
        try {
            return circleDatabase.getCircleIds(user_id);
        } catch (SQLException e) {
            return null;
        }
    }
}
