package com.life_pod.lifepod_app.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.api.Server;
import com.life_pod.lifepod_app.bluetooth.EventStore;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.AuthenticationServiceGrpc;
import pod.EventInterface;

import static com.life_pod.lifepod_app.bluetooth.EventStore.notificationIds;

/**
 * Firebase Messaging Service.
 * https://firebase.google.com/docs/cloud-messaging/android/client
 *
 */
public class LifePodMessagingService extends FirebaseMessagingService {

    public static String LOG_TAG = "LifePodMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO: 1/2/20 Server side notifications
        Log.v(LOG_TAG, "Message from: " +  remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            String title;
            String body;
            Log.v(LOG_TAG, "Message data payload: " + remoteMessage.getData());
            switch(Objects.requireNonNull(remoteMessage.getData().get("threat_priority"))) {
                case "0":
                    title = "LifePod Activated by " + remoteMessage.getData().get("username");
                    body =  String.format("%s has activated their LifePod. This could mean they think they are in danger. " +
                            "Be on alert for more notifications concerning the deactivation or release of the LifePod. Click To See Map.", remoteMessage.getData().get("username"));
                    break;
                case "2":
                    title = "URGENT: LifePod Released by " + remoteMessage.getData().get("username");
                    body = String.format("%s released their LifePod. This means they are in danger of a robbery of something worse." +
                            "If no deactivation comes, it is advised to contact the police with the coordinates of %s's location. Click To See Map.", remoteMessage.getData().get("username"), remoteMessage.getData().get("username"));
                    break;
                default:
                    title = "Something went wrong...";
                    body = "Ignore this Push. Something went wrong with our servers.";
            }
            // for google maps
            Constants.MapsData.latitude = remoteMessage.getData().get(Constants.KEY_LATITUDE);
            Constants.MapsData.longitude = remoteMessage.getData().get(Constants.KEY_LONGITUDE);
            Constants.MapsData.priority = remoteMessage.getData().get(Constants.KEY_PRIORITY);
            Constants.MapsData.username = remoteMessage.getData().get(Constants.KEY_USERNAME);
            // remove notification - if its deactivation
            if (Objects.requireNonNull(remoteMessage.getData().get(Constants.KEY_PRIORITY)).equals("1")) {
                Log.v(LOG_TAG, "GOT REMOVE MESAGE");
                handleNotificationDelete(remoteMessage.getData().get(Constants.KEY_USERNAME));
            } else {
                handleNotificationShow(title, body, remoteMessage.getData().get(Constants.KEY_USERNAME));
            }
        }
    }

    /**
     * Sends new token to server, so the server can update the token appropriately
     * for topic management.
     * @param token new Token
     */
    @Override
    public void onNewToken(String token) {
        // need method for initialization of token.
        sendTokenToServer(token);
    }

    static void sendTokenToServer(String token) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(Server.ip, Server.port).usePlaintext().build();
        AuthenticationServiceGrpc.AuthenticationServiceBlockingStub stub =
                AuthenticationServiceGrpc.newBlockingStub(managedChannel);
        EventInterface.RegistrationTokenRequest registrationTokenRequest = EventInterface.RegistrationTokenRequest.newBuilder()
                .setRegToken(token)
                .setUserId(EventStore.user_id)
                .build();
        if (stub.publishRegistrationToken(registrationTokenRequest).getResponse()) {
            Log.v(LOG_TAG, "New Token : " + token + ". Sent to server !");
        } else {
            Log.v(LOG_TAG, "There was a problem....");
        }
        try {
            managedChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "doInBackground: ", e);
        }
    }

    /**
     * Todo find notification thing
     * @param strUrl url
     * @return bitmap image
     */
    @Nullable
    static Bitmap getBitmapFromUrl(String strUrl) {
        try {
            URL url = new URL(strUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            Log.e(LOG_TAG, "getBitmapFromUrl: ", e);
            return null;
        }
    }

    void handleNotificationShow(String title, String body, String username) {
        Intent intent = new Intent(LifePodMessagingService.this, NotificationMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // put hashmap
        // TODO: 12/31/19 A hack happened, we are storing the data in a static class.
        //  Find out how to pass data to NotificationMapActivity - Fragman.
        // Simulate putting intent extras - we read this from NotificationMapActivity.

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, "lifePodChannelID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText("A LifePod Event Happened...")
                // TODO: 12/28/19 Notification images
//                    .setLargeIcon(icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId;

        if (notificationIds.size() != 0) {
            notificationId = notificationIds.get(0).id + 1;
        } else {
            notificationId = 0;
        }
        notificationIds.add(new EventStore.NotificationData(notificationId, username));
        notificationManager.notify(notificationId, notification);
    }

    void handleNotificationDelete(String target) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        for (EventStore.NotificationData data : notificationIds) {
            if (data.target.equals(target)) {
                Objects.requireNonNull(notificationManager).cancel(data.id);
            }
        }
    }


    static class Constants {
        static String KEY_LATITUDE = "latitude";
        static String KEY_LONGITUDE = "longitude";
        static String KEY_USERNAME = "username";
        static String KEY_PRIORITY = "threat_priority";

        static class MapsData {
            static String latitude;
            static String longitude;
            static String username;
            static String priority;
        }
    }
}
