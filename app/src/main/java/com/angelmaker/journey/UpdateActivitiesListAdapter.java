package com.angelmaker.journey;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by AngelPlayer on 5/8/2018.
 */

public class UpdateActivitiesListAdapter extends RecyclerView.Adapter<UpdateActivitiesListAdapter.ActivityViewHolder> {

    private ActivityViewModel activityViewModel;    //Test

    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView activityItemView;
        private final Button removeBtn;

        private ActivityViewHolder(View itemView) {
            super(itemView);
            activityItemView = itemView.findViewById(R.id.textView);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    private final LayoutInflater inflater;
    private List<ActivityInstance> activities; // Cached copy of words


    UpdateActivitiesListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    public void setViewModel(ActivityViewModel newActivityViewModel)
    {
        activityViewModel = newActivityViewModel;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_manage_activities, parent, false);
        return new ActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position)
    {
        if (activities != null)
        {
            final ActivityInstance current = activities.get(position);
            holder.activityItemView.setText(current.getActivity());

            holder.removeBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    activityViewModel.delete(current);;
                }
            });
        }

        else
            {
                // Covers the case of data not being ready yet.
                holder.activityItemView.setText("No Activities");
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

