package com.angelmaker.journey.supportFiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelmaker.journey.Activities.JourneyView;
import com.angelmaker.journey.R;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;

import java.util.ArrayList;
import java.util.List;

public class FinishedJourneysListAdapter extends RecyclerView.Adapter<FinishedJourneysListAdapter.ActivityViewHolder> {

    private ArrayList<ActivityType> finishedActivities;

    //Constructor that determines context to inflate in
    private final LayoutInflater inflater;
    public FinishedJourneysListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }


    public Activity androidActivity;
    public void setAndroidActivity(Activity newAndroidActivity){ androidActivity = newAndroidActivity;}

    //Initialize UI links
    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final LinearLayout finishedActivityTopLL;
        private final TextView activityNameTV;
        private final TextView startDateTV;
        private final TextView endDateTV;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            finishedActivityTopLL = itemView.findViewById(R.id.finishedActivityTopLL);
            activityNameTV = itemView.findViewById(R.id.finishedActivityNameTV);
            startDateTV = itemView.findViewById(R.id.startDateTV);
            endDateTV = itemView.findViewById(R.id.endDateTV);
        }
    }

    @Override //Select what form each recycle view should take
    public FinishedJourneysListAdapter.ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_finished_journeys, parent, false);
        return new ActivityViewHolder(itemView);
    }


    @Override //Code to set for each of the recycler views
    public void onBindViewHolder(FinishedJourneysListAdapter.ActivityViewHolder holder, int position) {
        if (finishedActivities != null)
        {
            final ActivityType activity = finishedActivities.get(position);

            holder.activityNameTV.setText(activity.getActivityTypeName());
            holder.startDateTV.setText("Start Date: " + activity.getStartDate());
            holder.endDateTV.setText("End Date:   " + activity.getEndDate());

            holder.finishedActivityTopLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent JourneyView = new Intent(androidActivity, JourneyView.class);
                    JourneyView.putExtra("EXTRA_ACTIVITY_NAME", activity.getActivityTypeName());
                    androidActivity.startActivity(JourneyView);
                }
            });
        }

        else
        {
            // Covers the case of data not being ready yet.
            holder.activityNameTV.setText("Loading...");
        }
    }

    @Override
    public int getItemCount() {
        if (finishedActivities != null)
            return finishedActivities.size();
        else return 0;    }


    public void setActivityNames(ArrayList<ActivityType> newActivityNames)
    {
        finishedActivities = newActivityNames;
        notifyDataSetChanged();
    }





/*
    //Sets button text to selected date and number of completed activities
    private class populateRecyclerView extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... lists) {
            //activityViewModel.getActivityDates
            return null;
        }

        @Override
        protected Void onPostExecute(Void voidd) {

        }
    }*/
}
