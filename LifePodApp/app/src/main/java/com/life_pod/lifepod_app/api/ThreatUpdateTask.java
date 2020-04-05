package com.life_pod.lifepod_app.api;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

import com.life_pod.lifepod_app.bluetooth.EventStore;
import com.life_pod.lifepod_app.location.LocationProvider;

import java.lang.ref.WeakReference;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.EventInterface;
import pod.EventServiceGrpc;

public class ThreatUpdateTask extends AsyncTask<Integer, Void, Void> {

    private WeakReference<Activity> activityWeakReference;
    private ManagedChannel managedChannel;

    public ThreatUpdateTask(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    // use integers[0]
    @Override
    protected Void doInBackground(Integer... integers) {
        /*
         FIXME: 12/23/19 When working with switching the method of alerting from network to SMS, (if that happens),
         Have the sms be done in the lambda.
         */
        int threatPriority = integers[0];
        LocationProvider.requestSingleUpdate(activityWeakReference.get().getApplicationContext(),
                location -> {
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    managedChannel = ManagedChannelBuilder.forAddress(Server.ip ,Server.port).usePlaintext().build();
                    EventServiceGrpc.EventServiceBlockingStub stub = EventServiceGrpc.newBlockingStub(managedChannel);
                    EventInterface.ThreatEvent threatRequest = EventInterface.ThreatEvent.newBuilder().setCoordinates(
                            EventInterface.GpsCoords.newBuilder().setLatitude(latitude).setLongitude(longitude).getDefaultInstanceForType())
                            .setUserId(EventStore.user_id).setThreatPriority(threatPriority).build();
                    EventInterface.ThreatPriorityResponse response = stub.updateThreatPriority(threatRequest);
                    if (!response.getStatus()) {
                        // TODO: 12/23/19 SMS notification to known contacts
                        // SMS because it failed. There is no way to display it on the UI, and
                        // it is useless for the product because we dont want a message on the
                        // phone saying it failed.
                        // lambdas are stateless.
                    }
                });
        return null;
    }
}
