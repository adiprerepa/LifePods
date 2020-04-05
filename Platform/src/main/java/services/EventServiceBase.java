package services;

import com.google.firebase.messaging.FirebaseMessagingException;
import pod.EventInterface;
import pod.EventServiceGrpc;
import interactor.ThreatInteractor;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 * gRPC service Implementation
 */

public class EventServiceBase extends EventServiceGrpc.EventServiceImplBase {

    private ThreatInteractor threatInteractor;

    public EventServiceBase(ThreatInteractor interactor) {
        this.threatInteractor = interactor;
    }

    // when threat updated
    @Override
    public void updateThreatPriority(EventInterface.ThreatEvent request, StreamObserver<EventInterface.ThreatPriorityResponse> responseObserver) {
        System.out.printf("Got Update Threat Priority request. params: %s\n", request.toString());
        EventInterface.ThreatPriorityResponse response = threatInteractor.handleThreatPriorityUpdate(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        System.out.println("Sent Response!");
        // send push notif
        try {
            threatInteractor.sendPush(request);
        } catch (FirebaseMessagingException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listEvents(EventInterface.EventIdentifier request, StreamObserver<EventInterface.ListEvent> responseObserver) {
        ArrayList<EventInterface.ThreatEvent> events = threatInteractor.getAllEvents(request.getUserId());
        EventInterface.ListEvent listEvent = EventInterface.ListEvent.newBuilder()
                .addAllEvents(events)
                .build();
        responseObserver.onNext(listEvent);
        responseObserver.onCompleted();
    }

    @Override
    public void getFirebaseNotificationTopics(EventInterface.FirebaseNotificationTopicRequest request, StreamObserver<EventInterface.FirebaseNotificationTopicResponse> responseObserver) {
        EventInterface.FirebaseNotificationTopicResponse response;
        ArrayList<String> circleIds = threatInteractor.getCircleNamesAssociatedWithUserId(request.getUserId());
        if (circleIds != null) {
            for (int i = 0; i < circleIds.size(); i++) {
                circleIds.set(i, circleIds.get(i).concat("_fcmtopic"));
                System.out.println(circleIds.get(i));
            }
            response = EventInterface.FirebaseNotificationTopicResponse.newBuilder().setHasTopics(true).addAllTopics(circleIds).build();
        } else {
            response = EventInterface.FirebaseNotificationTopicResponse.newBuilder().setHasTopics(false).addAllTopics(null).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
