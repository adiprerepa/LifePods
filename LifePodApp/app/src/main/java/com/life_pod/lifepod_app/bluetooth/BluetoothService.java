package com.life_pod.lifepod_app.bluetooth;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.LocationServices;
import com.life_pod.lifepod_app.api.Server;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pod.EventInterface;
import pod.EventServiceGrpc;

public class BluetoothService extends IntentService {

    private static String LOG_TAG = "BLUETOOTH_SERVICE";

    /**
     * Creates an IntentService.
     * name Used to name the worker thread, important only for debugging.
     */
    public BluetoothService() {
        super("bluetooth-service");
    }

    /**
     * TODO: 12/24/19
     * Right now, I think the timing between events should be done on the server.
     * Timing can be done on the client once the complexity reaches a point where
     * the case of no network/server offline is accounted for. If that is the case,
     * where network is not reachable, SMS or another form of communication can be sent.
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        BluetoothDevice bluetoothDevice = intent.getParcelableExtra("bonded_device");
        UUID uuid = (UUID) intent.getSerializableExtra("uuid");
        if (bluetoothDevice != null) {
            while (true) {
                try {
                    BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    if (!bluetoothSocket.isConnected()) bluetoothSocket.connect();
                    bluetoothSocket.getOutputStream().write("threatStatus".getBytes());
                    InputStream rpiInputStream = bluetoothSocket.getInputStream();
                    byte[] statusByte = new byte[1];
                    while (rpiInputStream.read(statusByte) == -1) {
                        try {
                            Thread.currentThread().wait(1);
                        } catch (InterruptedException e) {
                            Log.e(LOG_TAG, "Bluetooth Background service: ", e);
                        }
                    }
                    // there is a time param because in an earlier version we did
                    // the timing between events on the client.
                    //  && EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).isArm
                    //  && EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).statusRpi.isStatus()
                    if (new String(statusByte).equals("T") && EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).isArm) {
                        Log.v(LOG_TAG, "GOT A TRUE");
                        // insert activation
                        EventStore.eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), new BluetoothTask.ThreatStatusRpi(true, false), false, false));
                        // send activation
                        sendThreatUpdate(0);
                    } else if (new String(statusByte).equals("F") && EventStore.eventEntities.get(EventStore.eventEntities.size() - 1).statusRpi.isStatus()) {
                        // insert release
                        Log.v(LOG_TAG, "FUKCKINGIGNGI_________________________: GOT A FALSE");
                        EventStore.eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), new BluetoothTask.ThreatStatusRpi(false, false), false, false));
                        // send release
                        sendThreatUpdate(2);
                    }
                } catch (IOException e) {
//                    Log.e(LOG_TAG, "bluetoothService: ", e);
                }
            }
        }
    }

    void sendThreatUpdate(int threatPriority) {
        // TODO: 12/24/19 this is where the bug is, the location is not being acquired, and therefore not sent to the server.
        // try to do it another way.

        LocationServices.getFusedLocationProviderClient(this).getLastLocation().
                addOnSuccessListener(location -> {
                    Log.v("THISISLOCATIONNNN", "___________GOTLOGATION________________________________--");
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(Server.ip, Server.port).usePlaintext().build();
                    EventServiceGrpc.EventServiceBlockingStub stub = EventServiceGrpc.newBlockingStub(managedChannel);
                    EventInterface.ThreatEvent threatRequest = EventInterface.ThreatEvent.newBuilder().setCoordinates(
                            EventInterface.GpsCoords.newBuilder().setLatitude(latitude).setLongitude(longitude).build()
                    ).setUserId(EventStore.user_id).setThreatPriority(threatPriority).build();
                    EventInterface.ThreatPriorityResponse response = stub.updateThreatPriority(threatRequest);
                    try {
                        managedChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    // TODO: 12/24/19 clarse for network not reachable
                    if (!response.getStatus()) {
                        Log.i(LOG_TAG, "sendThreatUpdate: ");
                        /*
                          TODO: 12/23/19 SMS notification to known contacts
                           SMS because it failed. There is no way to display it on the UI, and
                           it is useless for the product because we dont want a message on the
                           phone saying it failed.
                           lambdas are stateless.
                         */

                    }
                });
    }
}
