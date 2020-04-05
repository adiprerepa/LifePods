package com.life_pod.lifepod_app.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.life_pod.lifepod_app.entities.LoginAttemptEntity;
import com.life_pod.lifepod_app.login.LoginInteractor;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.AuthenticationServiceGrpc;
import pod.EventInterface;

public class LoginTask extends AsyncTask<LoginAttemptEntity, Void, EventInterface.LoginResponse> {

    private WeakReference<Activity> activityWeakReference;
    private ManagedChannel managedChannel;
    private LoginInteractor loginInteractor;
    private LoginAttemptEntity currentLoginAttemptEntity;

    public LoginTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        loginInteractor = new LoginInteractor();
    }

    @Override
    protected EventInterface.LoginResponse doInBackground(LoginAttemptEntity... entities) {
        this.currentLoginAttemptEntity = entities[0];
        String username = entities[0].getUsername();
        String password = entities[0].getPassword();
        try {
            managedChannel = ManagedChannelBuilder.forAddress(Server.ip, Server.port).usePlaintext().build();
            AuthenticationServiceGrpc.AuthenticationServiceBlockingStub stub =
                    AuthenticationServiceGrpc.newBlockingStub(managedChannel);
            EventInterface.LoginRequest loginRequest = EventInterface.LoginRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .build();
            return stub.login(loginRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return EventInterface.LoginResponse.newBuilder().setStatus(false).setUserId("").build();
        }
    }

    @Override
    protected void onPostExecute(EventInterface.LoginResponse loginResponse) {
        // shut channel down
        try {
            managedChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        // change activity
        Activity activity = activityWeakReference.get();
        loginInteractor.handleLoginResponse(loginResponse, activity, currentLoginAttemptEntity);
    }
}
