package com.life_pod.lifepod_app.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.life_pod.lifepod_app.entities.RegisterAttemptEntity;
import com.life_pod.lifepod_app.register.RegisterInteractor;

import java.lang.ref.WeakReference;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.AuthenticationServiceGrpc;
import pod.EventInterface;

public class RegisterTask extends AsyncTask<RegisterAttemptEntity, Void, EventInterface.SignUpResponse> {

    private WeakReference<Activity> activityWeakReference;
    private RegisterInteractor registerInteractor;

    public RegisterTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
        registerInteractor = new RegisterInteractor();
    }

    @Override
    protected EventInterface.SignUpResponse doInBackground(RegisterAttemptEntity... entities) {
        try {
            ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(Server.ip, Server.port).usePlaintext().build();
            AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationServiceBlockingStub =
                    AuthenticationServiceGrpc.newBlockingStub(managedChannel);
            EventInterface.SignUpRequest signUpRequest = EventInterface.SignUpRequest.newBuilder()
                    .setUsername(entities[0].getUsername())
                    .setPassword(entities[0].getPassword())
                    .setEmailAddress(entities[0].getEmail())
                    .setPhoneNumber(entities[0].getPhoneNumber())
                    .build();
            return authenticationServiceBlockingStub.register(signUpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return EventInterface.SignUpResponse.newBuilder().setStatus(false).build();
        }
    }


    @Override
    protected void onPostExecute(EventInterface.SignUpResponse signUpResponse) {
        registerInteractor.handleRegisterResponse(signUpResponse, activityWeakReference.get());
    }
}
