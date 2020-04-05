package com.life_pod.lifepod_app.circle_switch;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.life_pod.lifepod_app.R;

import java.util.ArrayList;

public class CircleSwitcherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_switcher);
        ArrayList<String> circleStrArraylist = getIntent().getStringArrayListExtra("circleNames");
        ArrayList<CircleData> circleDataArrayList = new ArrayList<>();
        for (String str : circleStrArraylist) {
            circleDataArrayList.add(new CircleData(str));
        }
        RecyclerView recyclerView = findViewById(R.id.circleSwicher);
        CircleListAdapter adapter = new CircleListAdapter(circleDataArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}


/*
RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        MyListAdapter adapter = new MyListAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
 */