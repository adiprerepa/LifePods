package com.life_pod.lifepod_app.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.life_pod.lifepod_app.R;

import java.util.ArrayList;

// on recycler view click lead to event list
public class PodListAdapter extends RecyclerView.Adapter<PodEntityHolder> {

    private ArrayList<PodData> podData;

    public PodListAdapter(ArrayList<PodData> podData) {
        this.podData = podData;
    }

    @Override
    public PodEntityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.podentity, parent, false);
        return new PodEntityHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PodEntityHolder holder, int position) {
        holder.usernameTextView.setText(podData.get(position).getUsername());
        holder.locationTextView.setText(podData.get(position).getLastLocation());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // redirect to events screen
            }
        });
    }

    @Override
    public int getItemCount() {
        return podData.size();
    }
}
