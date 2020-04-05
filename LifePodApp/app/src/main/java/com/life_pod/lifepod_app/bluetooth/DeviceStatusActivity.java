package com.life_pod.lifepod_app.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.api.ThreatUpdateTask;

import java.util.Calendar;

import static com.life_pod.lifepod_app.bluetooth.EventStore.eventEntities;

public class DeviceStatusActivity extends AppCompatActivity {

    /**
     * TODO: 12/25/19 :) Merry Christmas!
     *  Even when the service stops, the activity still says that the device is connected.
     *  Maybe have BluetoothService publish a WeakReference to the BluetoothDevice,
     *  and we check the bluetoothDevice details there.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_status);
        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra("bonded_devices");
        TextView device_status = findViewById(R.id.device_status_tv);
        Button armButton = findViewById(R.id.arm_butt);
        Button deactivateButton = findViewById(R.id.deactivate_butt);
        if (bluetoothDevice != null) {
            TextView device_addr = findViewById(R.id.device_addr_tv);
            TextView device_name = findViewById(R.id.device_name_tv);
            device_status.setText(getString(R.string.device_status, "Connected"));
            device_addr.setText(getString(R.string.device_address, bluetoothDevice.getAddress()));
            device_name.setText(getString(R.string.device_name, bluetoothDevice.getName()));
        } else {
            device_status.setText(getString(R.string.device_status, "Not Connected"));
        }
        armButton.setOnClickListener(v -> {
            if (eventEntities.get(eventEntities.size() - 1).isDeactivation) {
                // make toast
                eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), new BluetoothTask.ThreatStatusRpi(false, false), true, false));
                showToast("Armed Successfully.");
            } else if (eventEntities.get(eventEntities.size() - 1).isArm) {
                showToast("Already Armed.");
            } else {
                showToast("Could Not Arm.");
            }
        });
        deactivateButton.setOnClickListener(v -> {
            if (!eventEntities.get(eventEntities.size() - 1).statusRpi.isStatus()) {
                eventEntities.add(new EventStore.ReleaseEventEntity(Calendar.getInstance().getTime(), new BluetoothTask.ThreatStatusRpi(false, false), false, true));
                // notify server.
                // bug here
                showToast("Deactivated Successfully.");
//                new ThreatUpdateTask(this)
//                        .execute(1);

            } else if (eventEntities.get(eventEntities.size() - 1).isDeactivation) {
                showToast("Already Deactivated.");
            } else {
                showToast("Could not deactivate.");
            }
        });
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
