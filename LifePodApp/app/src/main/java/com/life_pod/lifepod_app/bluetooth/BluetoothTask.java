package com.life_pod.lifepod_app.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.life_pod.lifepod_app.api.ThreatUpdateTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.UUID;

/**
 * @deprecated
 * Originally an AsyncTask, doesn't work for background services.
 * Started Using an IntentService in
 * @see BluetoothService
 */
public class BluetoothTask extends AsyncTask<BluetoothTask.BluetoothTaskInParam, Void, BluetoothTask.ThreatStatusRpi> {

    private WeakReference<Activity> activityWeakReference;

    public BluetoothTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    // use bluetoothDevices[0]
    @Override
    protected ThreatStatusRpi doInBackground(BluetoothTaskInParam... taskObject) {
        String LOG_TAG = "BLUETOOTH_TASK";
        try {
            BluetoothSocket bluetoothSocket = taskObject[0].rpiDevice.createRfcommSocketToServiceRecord(taskObject[0].rpiUUID);
            if (!bluetoothSocket.isConnected()) bluetoothSocket.connect();
            bluetoothSocket.getOutputStream().write("threatStatus".getBytes());
            InputStream rpiInputStream = bluetoothSocket.getInputStream();
            byte[] statusByte = new byte[1];
            while (rpiInputStream.read(statusByte) == -1) {
                try {
                    Thread.currentThread().wait(100);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "doInBackground Thread: ", e);
                }
            }
            if (new String(statusByte).equals("T")) {
                return new ThreatStatusRpi(false, false);
            } else {
                return new ThreatStatusRpi(true, false);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "doInBackground: ", e);
            return new ThreatStatusRpi(false, true);
        }
    }

    @Override
    protected void onPostExecute(ThreatStatusRpi status) {
        // ThreatUpdateTask
        if (!status.err) {
            // we ignore repeated stuff this way
            if (EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).isArm && status.status) {
                // todo send to activation screen
                // code 0
                EventStore.eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), status, false, false));
                new ThreatUpdateTask(activityWeakReference.get())
                        .execute(0);

            } else if (EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).statusRpi.status && !status.status) {
                // insert release - code 2
                EventStore.eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), status, false, false));
                new ThreatUpdateTask(activityWeakReference.get())
                        .execute(2);
                // todo release screen
            } else if (EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).isDeactivation) {
                new ThreatUpdateTask(activityWeakReference.get())
                        .execute(1);
                // todo deactivation screen
            }

            // if the last one was a release and the time between events has exceeded 10 seconds
            if (!EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).statusRpi.status &&
                    (Calendar.getInstance().getTime().getTime() - EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).timeOfEvent.getTime() >= 10000)) {
                // release no deactivation - code 3
                new ThreatUpdateTask(activityWeakReference.get())
                        .execute(3);
                // todo back to home
            }
        }
    }

    public static class BluetoothTaskInParam {

        private UUID rpiUUID;
        private BluetoothDevice rpiDevice;

        public BluetoothTaskInParam(UUID rpiUUID, BluetoothDevice rpiDevice) {
            this.rpiUUID = rpiUUID;
            this.rpiDevice = rpiDevice;
        }

        public UUID getRpiUUID() {
            return rpiUUID;
        }

        public void setRpiUUID(UUID rpiUUID) {
            this.rpiUUID = rpiUUID;
        }

        public BluetoothDevice getRpiDevice() {
            return rpiDevice;
        }

        public void setRpiDevice(BluetoothDevice rpiDevice) {
            this.rpiDevice = rpiDevice;
        }
    }

    static class ThreatStatusRpi {

        private boolean status;
        private boolean err;

        public ThreatStatusRpi(boolean status, boolean err) {
            this.status = status;
            this.err = err;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public boolean isErr() {
            return err;
        }

        public void setErr(boolean err) {
            this.err = err;
        }
    }
}
