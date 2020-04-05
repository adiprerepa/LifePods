package api;

import database.authentication.AuthenticationDatabase;
import database.circles.CircleDatabase;
import database.event.EventDatabase;
import interactor.FirebaseInteractor;
import interactor.ThreatInteractor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.EventServiceBase;
import services.LoginServiceBase;
import java.io.IOException;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 */

public class ServiceApi {

    private Server server;

    public void start(int port, String url, String username, String pass) {
        String tablename = "circles";
        CircleDatabase circleDatabase = new CircleDatabase(url, username, pass, tablename);
        EventDatabase eventDatabase = new EventDatabase(url, username, pass, "events");
        AuthenticationDatabase authenticationDatabase = new AuthenticationDatabase(url, username, pass, "users");
        FirebaseInteractor.initializeFirebase();
        try {
            server = ServerBuilder
                    .forPort(port)
                    .addService(new EventServiceBase(new ThreatInteractor(eventDatabase, authenticationDatabase, circleDatabase)))
                    .addService(new LoginServiceBase(circleDatabase, authenticationDatabase))
                    .build()
                    .start();
            System.out.printf("Server Started on port %d..\n", port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.err.println("JVM SHIT UP, BYE");
                ServiceApi.this.stopServer();
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void blockUntilShutdown() throws Throwable {
        if (server != null) server.awaitTermination();
    }

    private void stopServer() {
        if (server != null) server.shutdown();
    }
}
