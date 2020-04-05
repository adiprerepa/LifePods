package com.life_pod.lifepod_app.bluetooth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventStore {

    public static ArrayList<ReleaseEventEntity> eventEntities = new ArrayList<>();
    public static String user_id;
    public static ArrayList<NotificationData> notificationIds = new ArrayList<>();

    public static void init() {
        eventEntities.add(0, new ReleaseEventEntity(Calendar.getInstance().getTime(), new BluetoothTask.ThreatStatusRpi(false, false), true, false));
    }

    static class ReleaseEventEntity {

        Date timeOfEvent;
        BluetoothTask.ThreatStatusRpi statusRpi;
        boolean isArm;
        boolean isDeactivation;

        ReleaseEventEntity(Date timeOfEvent, BluetoothTask.ThreatStatusRpi statusRpi, boolean isArm, boolean isDeactivation) {
            this.timeOfEvent = timeOfEvent;
            this.statusRpi = statusRpi;
            this.isArm = isArm;
            this.isDeactivation = isDeactivation;
        }
    }

    public static class NotificationData {

        public int id;
        public String target;

        public NotificationData(int id, String target) {
            this.id = id;
            this.target = target;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }
}
