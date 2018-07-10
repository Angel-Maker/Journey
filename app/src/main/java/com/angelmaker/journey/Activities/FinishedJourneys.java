package com.angelmaker.journey.Activities;


import android.arch.lifecycle.ViewModel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.FinishedJourneysListAdapter;
import com.angelmaker.journeyDatabase.ActivityType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class FinishedJourneys extends AppCompatActivity {

    private FinishedJourneysListAdapter adapter = null;
    ArrayList<ActivityType> finishedActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_journeys);

        finishedActivities = (ArrayList<ActivityType>) getIntent().getSerializableExtra("EXTRA_FINISHED_ACTIVITIES");

        setRecyclerView();
    }

    private void setRecyclerView()
    {
        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.finishedJourneysRV);
        final FinishedJourneysListAdapter adapter = new FinishedJourneysListAdapter(this);
        adapter.setAndroidActivity(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add dividers between cells
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        //Pass in data to build list around
        adapter.setActivityNames(finishedActivities);
    }
}


