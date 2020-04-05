package com.life_pod.lifepod_app.notification;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Sole purpose is the initial sending of the token.
 * If the Token changes, it is handled in LifePodMessagingService,
 * when onTokenChange() is called.
 */
public class UpdateFirebaseTokenTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        Log.v("FirebaseTokenTask", "TOKEN: " + strings[0]);
        LifePodMessagingService.sendTokenToServer(strings[0]);
        return null;
    }
}