package com.angelmaker.journey;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.util.List;

public class UpdateActivities extends AppCompatActivity {

    private ActivityViewModel activityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_activities);

        //Set viewmodel for database manipulation
        activityViewModel = new ActivityViewModel(getApplication());

        setRecyclerView();
    }

    private void setRecyclerView()
    {
        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.activitiesRV);
        final UpdateActivitiesListAdapter adapter = new UpdateActivitiesListAdapter(this);
        adapter.setViewModel(activityViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        //Link view model to database
        activityViewModel.getActivityNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> activityNames) {
                //Executed whenever the observed object changes
                adapter.setActivityNames(activityNames);   // Update the cached copy of the words in the adapter.
            }
        });


    }


    public void newActivityBtn(View view) {
        Intent newActivity = new Intent(this, NewActivity.class);
        startActivity(newActivity);
    }
}
