package services;

import database.authentication.AuthenticationDatabase;
import database.authentication.entities.AuthenticationDatabaseEntity;
import database.authentication.entities.AuthenticationDatabaseIdentifier;
import database.circles.CircleDatabase;
import database.circles.circle_entities.CircleDatabaseEntity;
import database.circles.circle_entities.CircleDatabaseIdentifier;
import database.circles.circle_entities.CircleEntity;
import io.grpc.stub.StreamObserver;
import pod.AuthenticationServiceGrpc;
import pod.EventInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 * gRPC service Implementation
 */

public class LoginServiceBase extends AuthenticationServiceGrpc.AuthenticationServiceImplBase {

    private CircleDatabase circleDatabase;
    private AuthenticationDatabase authenticationDatabase;

    public LoginServiceBase(CircleDatabase circleDatabase, AuthenticationDatabase authenticationDatabase) {
        this.circleDatabase = circleDatabase;
        this.authenticationDatabase = authenticationDatabase;
    }

    @Override
    public void circleJoin(EventInterface.CircleRegisterRequest request, StreamObserver<EventInterface.CircleRegisterResponse> responseObserver) {
        CircleDatabaseIdentifier<String> identifier = new CircleDatabaseIdentifier<>(request.getCircleCode());
        EventInterface.CircleRegisterResponse registerResponse;
        try {
            if (circleDatabase.authenticate(identifier)) {
                // could verify circleid
                // retrieve users assoc. with circleid
                ArrayList<CircleDatabaseEntity> circles = circleDatabase.retrieveEntity(identifier);
                // name should be the same - 0 is arbitrary
                String circleName = circles.get(0).getCircleName();
                ArrayList<EventInterface.CircleUser> users = new ArrayList<>();
                // add protobuf defined classes from custom java ones we retrieved
                for (CircleDatabaseEntity entity : circles) {
                    users.add(EventInterface.CircleUser.newBuilder()
                            .setUsername(entity.getUsername())
                            .build());
                }
                // set registerresp with a circle with those users
                registerResponse = EventInterface.CircleRegisterResponse.newBuilder()
                        .setCircleResponse(
                                EventInterface.Circle.newBuilder()
                                        .addAllCircleUsers(users)
                                        .setCircleName(circleName)
                                        .build())
                        .build();
            } else {
                // null if not auth
                registerResponse = EventInterface.CircleRegisterResponse.newBuilder().setCircleResponse((EventInterface.Circle) null).build();
            }
            responseObserver.onNext(registerResponse);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            registerResponse = EventInterface.CircleRegisterResponse.newBuilder().setCircleResponse((EventInterface.Circle) null).build();
            responseObserver.onNext(registerResponse);
            responseObserver.onCompleted();
            e.printStackTrace();
        }
    }

    @Override
    public void register(EventInterface.SignUpRequest request, StreamObserver<EventInterface.SignUpResponse> responseObserver) {
        System.out.println("Register req: " + request.toString());
        EventInterface.SignUpResponse signUpResponse;
        AuthenticationDatabaseEntity authenticationDatabaseEntity = new AuthenticationDatabaseEntity(request.getUsername(), request.getPassword(), UUID.randomUUID().toString(),
                request.getEmailAddress(), request.getPhoneNumber());
        try {
            authenticationDatabase.insert(authenticationDatabaseEntity);
            signUpResponse = EventInterface.SignUpResponse.newBuilder().setStatus(true).build();
        } catch (SQLException e) {
            signUpResponse = EventInterface.SignUpResponse.newBuilder().setStatus(false).build();
            e.printStackTrace();
        }
        responseObserver.onNext(signUpResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void login(EventInterface.LoginRequest request, StreamObserver<EventInterface.LoginResponse> responseObserver) {
        // only send back circles  if authentication successful
        System.out.println("login req: " + request.toString());
        EventInterface.LoginResponse response;
        authenticationDatabase.injectPassword(request.getPassword());
        ArrayList<EventInterface.Circle> pbCircles = new ArrayList<>();
        try {
            System.out.println("here");
            if (authenticationDatabase.authenticate(new AuthenticationDatabaseIdentifier<>(request.getUsername()))) {
                System.out.printf("Authenticated : %s\n", request.getUsername());
                // handle size 0 circle list on client
                ArrayList<CircleEntity> circleEntities = circleDatabase.retrieveCircleList(new CircleDatabaseIdentifier<>(request.getUsername()));
                System.out.printf("Numbers of Circles associated with %s : %s\n", request.getUsername(), circleEntities.size());
                // if there are more than 0 circles
                if (circleEntities.size() > 0) {
                    for (CircleEntity circleEntity : circleEntities) {
                        ArrayList<EventInterface.CircleUser> circleUsers = new ArrayList<>();
                        // convert to pb
                        for (String c : circleEntity.getUsers()) {
                            circleUsers.add(EventInterface.CircleUser.newBuilder().setUsername(c).build());
                        }
                        System.out.println(circleEntity.getCircleName());
                        assert circleEntity.getCircleName() != null : "Fuck where did i mess up";
                        EventInterface.Circle pbCircle = EventInterface.Circle.newBuilder()
                                .addAllCircleUsers(circleUsers)
                                .setCircleName("First Circle")
                                .build();
                        pbCircles.add(pbCircle);
                    }
                    response = EventInterface.LoginResponse.newBuilder().addAllResponseCircles(pbCircles).setUserId(authenticationDatabase.getUserId()).setStatus(true).build();
                } else {
                    response = EventInterface.LoginResponse.newBuilder().addAllResponseCircles(new ArrayList<>()).setUserId(authenticationDatabase.getUserId()).setStatus(true).build();
                }
                System.out.println("Sent");
            } else {
                response = EventInterface.LoginResponse.newBuilder().addAllResponseCircles(new ArrayList<>()).setUserId("").setStatus(false).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response = EventInterface.LoginResponse.newBuilder().addAllResponseCircles(new ArrayList<>()).setUserId("").setStatus(false).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void circleCreate(EventInterface.CircleCreateRequest request, StreamObserver<EventInterface.CircleCreateResponse> responseObserver) {
        EventInterface.CircleCreateResponse response;
        CircleDatabaseEntity circleDatabaseEntity = new CircleDatabaseEntity(request.getUserId(), request.getCircleName(), UUID.randomUUID().toString(), request.getCreatingUser());
        try {
            circleDatabase.insert(circleDatabaseEntity);
            // got this far w/o error
            response = EventInterface.CircleCreateResponse.newBuilder().setSuccess(true).build();
        } catch (SQLException e) {
            // id probably exists already
            response = EventInterface.CircleCreateResponse.newBuilder().setSuccess(false).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void publishRegistrationToken(EventInterface.RegistrationTokenRequest request, StreamObserver<EventInterface.RegistrationTokenResponse> responseObserver) {
        EventInterface.RegistrationTokenResponse registrationTokenResponse;
        try {
            authenticationDatabase.insertRegistrationToken(request.getUserId(), request.getRegToken());
            registrationTokenResponse = EventInterface.RegistrationTokenResponse.newBuilder().setResponse(true).build();
        } catch (SQLException e) {
            registrationTokenResponse = EventInterface.RegistrationTokenResponse.newBuilder().setResponse(false).build();
        }
        responseObserver.onNext(registrationTokenResponse);
        responseObserver.onCompleted();
    }
}
