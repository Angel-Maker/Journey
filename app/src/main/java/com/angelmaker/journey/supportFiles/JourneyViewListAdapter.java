package com.angelmaker.journey.supportFiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelmaker.journey.R;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class JourneyViewListAdapter extends RecyclerView.Adapter<JourneyViewListAdapter.ActivityViewHolder>{

    private List<ActivityInstance> activities;

    //Constructor that determines context to inflate in
    private final LayoutInflater inflater;
    public JourneyViewListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    //Initialize UI links
    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView fileNameTV;
        private final LinearLayout containingLL;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            fileNameTV = itemView.findViewById(R.id.fileNameTV);
            containingLL = itemView.findViewById(R.id.containingLL);
        }
    }


    //Select what form each recycle view should take
    @Override
    public JourneyViewListAdapter.ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_journey_view, parent, false);
        return new ActivityViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull JourneyViewListAdapter.ActivityViewHolder holder, int position) {
        if (activities != null)
        {
            final ActivityInstance activity = activities.get(position);

            Uri uri = Uri.parse(activity.getAssociatedFile());

            String fileName = DailyActivitiesListAdapter.getFileName(uri, androidActivity);
            holder.fileNameTV.setText(fileName);

            holder.fileNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openFile = new Intent();
                    openFile.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse(activity.getAssociatedFile());
                    openFile.setData(uri);
                    openFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    androidActivity.startActivity(openFile);
                }
            });

            if(activity.getStar()){
                holder.containingLL.setBackgroundColor(0xFFFFDF00);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (activities != null)
            return activities.size();
        else return 0;
    }



    public void setActivities(List<ActivityInstance> newActivities)
    {
        activities = newActivities;
        notifyDataSetChanged();
    }

    public Activity androidActivity;
    public void setAndroidActivity(Activity newAndroidActivity){ androidActivity = newAndroidActivity;}
}
