package com.life_pod.lifepod_app.login.login_response;

import java.io.Serializable;
import java.util.ArrayList;

// for protobuf - they dont have serialization inbuilt, so we
// have to do it.
public class SerializableResponse implements Serializable {

    private ArrayList<SerializableCircle> circles;
    private boolean status;

    public SerializableResponse() { }

    public SerializableResponse(ArrayList<SerializableCircle> circles, boolean status) {
        this.circles = circles;
        this.status = status;
    }

    public ArrayList<SerializableCircle> getCircles() {
        return circles;
    }

    public void setCircles(ArrayList<SerializableCircle> circles) {
        this.circles = circles;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean notNull() {
        return !circles.isEmpty();
    }
}
