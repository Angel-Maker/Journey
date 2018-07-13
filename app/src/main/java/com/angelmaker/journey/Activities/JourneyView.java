package com.angelmaker.journey.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.FinishedJourneysListAdapter;
import com.angelmaker.journey.supportFiles.JourneyViewListAdapter;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;

import java.util.ArrayList;
import java.util.List;

public class JourneyView extends AppCompatActivity {

    private ActivityViewModel activityViewModel;
    private TextView journeyTitleTV;
    JourneyViewListAdapter adapter;

    String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_view);

        journeyTitleTV = findViewById(R.id.journeyTitleTV);

        activityName = getIntent().getStringExtra("EXTRA_ACTIVITY_NAME");
        journeyTitleTV.setText("Your Journey for: " + activityName);

        //Initialize view model
        activityViewModel = new ActivityViewModel(getApplication());

        setRecyclerView();
        new populateList().execute(activityName);
    }

    private void setRecyclerView()
    {
        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.journeyViewRV);
        adapter = new JourneyViewListAdapter(this);
        adapter.setAndroidActivity(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add dividers between cells
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }


    //Sets button text to selected date and number of completed activities
    private class populateList extends AsyncTask<String, Void, List<ActivityInstance>> {
        @Override
        protected List<ActivityInstance> doInBackground(final String... lists)
        {
            List<ActivityInstance> allActivities = activityViewModel.getNamedActivityInstances(lists[0]);
            List<ActivityInstance> newActivities = new ArrayList<>();

            for (int i = 1 ; i < allActivities.size() ; i++){
                ActivityInstance activity = allActivities.get(i);

                if(activity.getAssociatedFile() != null)
                {
                    newActivities.add(activity);
                }
            }

            return newActivities;
        }

        @Override
        protected void onPostExecute(List<ActivityInstance> newActivities)
        {
            //Pass in data to build list around
            adapter.setActivities(newActivities);
        }
    }
}
