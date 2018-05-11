package com.angelmaker.journey;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.util.List;

/**
 * Created by AngelPlayer on 5/8/2018.
 */

public class DailyActivitiesListAdapter extends RecyclerView.Adapter<DailyActivitiesListAdapter.ActivityViewHolder> {

    private ActivityViewModel activityViewModel;

    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final CheckBox completionCB;
        private final TextView fileTV;
        private final Button removeFileBtn;

        //Map recycler view entities to variables
        private ActivityViewHolder(View itemView) {
            super(itemView);
            completionCB = itemView.findViewById(R.id.completionCB);
            fileTV = itemView.findViewById(R.id.fileTV);
            removeFileBtn = itemView.findViewById(R.id.removeFileBtn);
        }
    }

    private final LayoutInflater inflater;
    private List<ActivityInstance> activities; // Cached copy of words

    DailyActivitiesListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    //Sets view model so adapter can modify database content
    public void setViewModel(ActivityViewModel newActivityViewModel)
    {
        activityViewModel = newActivityViewModel;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_daily_activities, parent, false);
        return new ActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position)
    {
        if (activities != null)
        {
            final ActivityInstance current = activities.get(position);
            holder.completionCB.setText(current.getActivity());
        }

        else
        {
            // Covers the case of data not being ready yet.
            holder.completionCB.setText("No Activities");
        }
    }

    void setActivities(List<ActivityInstance> newActivities)
    {
        activities = newActivities;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount()
    {
        if (activities != null)
            return activities.size();
        else return 0;
    }
}

