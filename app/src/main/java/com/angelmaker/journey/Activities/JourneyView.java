package com.angelmaker.journey.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.FinishedJourneysListAdapter;
import com.angelmaker.journeyDatabase.ActivityInstance;

import java.util.List;

public class JourneyView extends AppCompatActivity {

    private ActivityViewModel activityViewModel;
    private TextView journeyTitleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_view);

        journeyTitleTV = findViewById(R.id.journeyTitleTV);

        String activityName = getIntent().getStringExtra("EXTRA_ACTIVITY_NAME");
        journeyTitleTV.setText("Your Journey for: " + activityName);


        //Initialize view model
        activityViewModel = new ActivityViewModel(getApplication());

        //setRecyclerView();
    }

    private void setRecyclerView()
    {
        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.finishedJourneysRV);
        final FinishedJourneysListAdapter adapter = new FinishedJourneysListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add dividers between cells
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        List<ActivityInstance> activities = activityViewModel.getNamedActivityInstances("name");

        //Pass in data to build list around
        //adapter.setActivityNames(activities);
    }
}
