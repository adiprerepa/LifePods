package com.life_pod.lifepod_app.home;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.life_pod.lifepod_app.R;

public class PodEntityHolder extends RecyclerView.ViewHolder {

    public TextView usernameTextView;
    public TextView locationTextView;
    public RelativeLayout relativeLayout;

    public PodEntityHolder(@NonNull View itemView) {
        super(itemView);
        this.usernameTextView = (TextView) itemView.findViewById(R.id.username_rv);
        this.locationTextView = (TextView) itemView.findViewById(R.id.coordinates_rv);
        this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.podRecyclerView);
    }
}
