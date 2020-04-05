package com.life_pod.lifepod_app.home;

import com.life_pod.lifepod_app.login.login_response.SerializableCircle;
import com.life_pod.lifepod_app.login.login_response.SerializableCircleUser;
import com.life_pod.lifepod_app.login.login_response.SerializableResponse;

import java.util.ArrayList;

import pod.EventInterface;

class HomeInteractor {

    ArrayList<Circle> getPodData(EventInterface.LoginResponse response) {
        ArrayList<Circle> circleUsers = new ArrayList<>();
        for (EventInterface.Circle circle : response.getResponseCirclesList()) {
            Circle tmpCircle = new Circle();
            tmpCircle.setCircleName(circle.getCircleName());
            tmpCircle.setPodData(new ArrayList<PodData>());
            for (EventInterface.CircleUser circleUser : circle.getCircleUsersList()) {
                // TODO: 1/1/20 Stop hard-coding the coordinates and get the real location
                // TODO: 1/1/20 Start doing RGC (Reverse GeoCode) for street addresses.
                String strCoordinates = "42.0, 422.0";
                tmpCircle.getPodData().add(new PodData(circleUser.getUsername(), strCoordinates));
            }
            circleUsers.add(tmpCircle);
        }
        return circleUsers;
    }

    ArrayList<String> getCircleNames(EventInterface.LoginResponse response) {
        ArrayList<String> circleNames = new ArrayList<>();
        for (EventInterface.Circle c : response.getResponseCirclesList()) {
            circleNames.add(c.getCircleName());
        }
        return circleNames;
    }

    EventInterface.LoginResponse getLoginResponseFromSerializableMessage(SerializableResponse response) {
        ArrayList<EventInterface.Circle> responseCircles = new ArrayList<>();
        for (SerializableCircle serializableCircle : response.getCircles()) {
            ArrayList<EventInterface.CircleUser> circleUsers = new ArrayList<>();
            for (SerializableCircleUser serializableCircleUser : serializableCircle.getUsers()) {
                circleUsers.add(EventInterface.CircleUser.newBuilder()
//                        .setLastCoordinates(EventInterface.GpsCoords.newBuilder().setLatitude(42.0).setLatitude(43.0).build())
                        .setUsername(serializableCircleUser.getUsername())
                        .build());
            }
            responseCircles.add(EventInterface.Circle.newBuilder().addAllCircleUsers(circleUsers).setCircleName(serializableCircle.getCircleName()).build());
        }
        return EventInterface.LoginResponse.newBuilder().addAllResponseCircles(responseCircles).setStatus(response.isStatus()).setUserId("").build();

    }
}