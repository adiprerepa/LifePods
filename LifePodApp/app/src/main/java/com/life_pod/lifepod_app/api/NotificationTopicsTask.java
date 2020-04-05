package com.life_pod.lifepod_app.api;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.life_pod.lifepod_app.MainActivity;
import com.life_pod.lifepod_app.bluetooth.EventStore;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.EventInterface;
import pod.EventServiceGrpc;

public class NotificationTopicsTask extends AsyncTask<Void, Void, Void> {

    public static String LOG_TAG = "NotificationTopicsTask";

    /**
     * Get notification topics for the user_id in EventStore.
     */
    @Override
    protected Void doInBackground(Void... voids) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(Server.ip, Server.port).usePlaintext().build();
        EventServiceGrpc.EventServiceBlockingStub stub = EventServiceGrpc.newBlockingStub(managedChannel);
        EventInterface.FirebaseNotificationTopicResponse response = stub.getFirebaseNotificationTopics(
                EventInterface.FirebaseNotificationTopicRequest.newBuilder().setUserId(EventStore.user_id).build());
        if (response.getHasTopics()) {
            for (String topic : response.getTopicsList()) {
                Log.v(LOG_TAG, topic);
                subscribeFCM(topic);

            }
        }
        try {
            managedChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "doInBackground: ", e);
        }
        // TODO: 12/26/19 error handling for no notification topics
        //  Happens because no circles are associated with the the user_id
        //  in EventStore.

        return null;
    }

    static void subscribeFCM(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(LOG_TAG, "subscribeFCM: ", task.getException());
            } else {
                Log.v(LOG_TAG, "SUCCESSFUL! SUBSCRIBED BITCH!");
            }
        });
    }
}
