package com.life_pod.lifepod_app.circle_switch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.life_pod.lifepod_app.R;

public class CircleEntityHolder extends RecyclerView.ViewHolder {

    public TextView circleNameTextView;

    public CircleEntityHolder(@NonNull View itemView) {
        super(itemView);
        circleNameTextView = itemView.findViewById(R.id.circleName);
    }
}
