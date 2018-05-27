package com.angelmaker.journey.supportFiles;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.angelmaker.journey.ActivityViewModel;
import com.angelmaker.journey.NewActivity;
import com.angelmaker.journey.R;

import java.io.File;
import java.util.List;

/**
 * Created by AngelPlayer on 5/8/2018.
 */

public class UpdateActivitiesListAdapter extends RecyclerView.Adapter<UpdateActivitiesListAdapter.ActivityViewHolder> {

    private ActivityViewModel activityViewModel;

    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView activityNameTV;
        private final Button removeBtn;
        private final ImageButton editBtn;

        private ActivityViewHolder(View itemView) {
            super(itemView);
            activityNameTV = itemView.findViewById(R.id.activityNameTV);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
        }
    }

    private final LayoutInflater inflater;
    private List<String> activityNames; // Cached copy of words


    public UpdateActivitiesListAdapter(Context context)
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
        if (activityNames != null)
        {
            final String current = activityNames.get(position);
            holder.activityNameTV.setText(current);

            //Delete button
            holder.removeBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { builder = new AlertDialog.Builder(androidActivity, android.R.style.Theme_Material_Dialog_Alert); }
                    else { builder = new AlertDialog.Builder(androidActivity); }

                    builder.setTitle("Activity deletion warning!")
                            .setMessage("Once an activity is deleted it cannot be recovered.\n\nDo you want to proceed?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteActivityFolder(current);
                                    activityViewModel.deleteFullActivity(current);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            //Edit button
            holder.editBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent newActivity = new Intent(androidActivity, NewActivity.class);
                    newActivity.putExtra("activityName", current);
                    androidActivity.startActivity(newActivity);
                }
            });
        }

        else
            {
                // Covers the case of data not being ready yet.
                holder.activityNameTV.setText("No Activities");
            }
    }

    private void deleteActivityFolder(String activityName){
        File folder = new File(androidActivity.getApplicationContext().getFilesDir() + "/linked_files/" + activityName);
        deleteRecursive(folder);
    }

    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }


    public Activity androidActivity;
    public void setAndroidActivity(Activity newAndroidActivity){ androidActivity = newAndroidActivity;}

    public void setActivityNames(List<String> newActivityNames)
    {
        activityNames = newActivityNames;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount()
    {
        if (activityNames != null)
            return activityNames.size();
        else return 0;
    }
}

