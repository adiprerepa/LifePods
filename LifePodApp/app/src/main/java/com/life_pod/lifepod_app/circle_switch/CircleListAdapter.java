package com.life_pod.lifepod_app.circle_switch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.life_pod.lifepod_app.R;
import com.life_pod.lifepod_app.home.HomeActivity;

import java.util.ArrayList;

public class CircleListAdapter extends RecyclerView.Adapter<CircleEntityHolder> {

    private Context context;
    private ArrayList<CircleData> circleData;

    public CircleListAdapter(ArrayList<CircleData> circleData) {
        this.circleData = circleData;
    }

    @NonNull
    @Override
    public CircleEntityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        this.context = parent.getContext();
        View listItem = layoutInflater.inflate(R.layout.circle_element, parent, false);
        return new CircleEntityHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final CircleEntityHolder holder, int position) {
        holder.circleNameTextView.setText(circleData.get(position).getCircleName());
        holder.circleNameTextView.setOnClickListener(view -> {
            // set circle in intent and homeActivity will handle
            // handle circle Switch
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("circleName", holder.circleNameTextView.getText().toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return circleData.size();
    }
}
