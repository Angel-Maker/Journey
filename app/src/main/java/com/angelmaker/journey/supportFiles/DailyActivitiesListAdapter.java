package com.angelmaker.journey.supportFiles;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.angelmaker.journey.Activities.ActivityViewModel;
import com.angelmaker.journey.R;
import com.angelmaker.journeyDatabase.ActivityDao;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AngelPlayer on 5/8/2018.
 */


public class DailyActivitiesListAdapter extends RecyclerView.Adapter<DailyActivitiesListAdapter.ActivityViewHolder> {

    private ActivityViewModel activityViewModel;

    private static ExpandableListView expListView;
    private static ExpandableListAdapter listAdapter;
    static List<String> listDataHeader;
    static HashMap<String, List<String>> listDataChild;


    class ActivityViewHolder extends RecyclerView.ViewHolder
    {
        private final CheckBox completionCB;
        private final CheckBox starCB;
        private final TextView fileTV;
        private final Button removeFileBtn;

        //Map recycler view entities to variables
        private ActivityViewHolder(View itemView) {
            super(itemView);
            completionCB = itemView.findViewById(R.id.completionCB);
            starCB = itemView.findViewById(R.id.starCB);
            fileTV = itemView.findViewById(R.id.fileTV);
            removeFileBtn = itemView.findViewById(R.id.removeFileBtn);

            expListView = itemView.findViewById(R.id.descriptionETV);
        }
    }







//Generates description
    private static void prepareListData(String activityDescription) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Description");

        // Adding child data
        List<String> description = new ArrayList<>();

        if(activityDescription != null && !activityDescription.equals("")){description.add(activityDescription);}
        else{description.add("-----No description entered-----\nUse the update activities' edit button to add one!");}


        listDataChild.put(listDataHeader.get(0), description);
    }


    public static void setupExpandableView() {
        listAdapter = new DailyActivitiesExpandableListAdapter(androidActivity.getApplicationContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        final float scale = androidActivity.getApplicationContext().getResources().getDisplayMetrics().density; //Convert pixel to dp:  (int)(dps * scale + 0.5f);

        final ViewGroup.LayoutParams lp = expListView.getLayoutParams();

        //Increase view area when clicking on description
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                lp.height = (int)(50 * scale + 0.5f);
            }
        });

        //Decrease view area when collapsing on description
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                lp.height = (int)(13 * scale + 0.5f);
            }
        });
    }

    private final LayoutInflater inflater;
    private List<ActivityInstance> activities; // Cached copy of words

    public DailyActivitiesListAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    //Sets view model so adapter can modify database content
    public void setViewModel(ActivityViewModel newActivityViewModel)
    {
        activityViewModel = newActivityViewModel;
    }


    public void descriptionListSetup (String activityName) {
        new descriptionListSetupAsyncTask(activityViewModel).execute(activityName);
    }

    private static class descriptionListSetupAsyncTask extends AsyncTask<String, Void, Void> {

        private ActivityViewModel asyncTaskActivityViewModel;

        descriptionListSetupAsyncTask(ActivityViewModel activityViewModel) {
            asyncTaskActivityViewModel = activityViewModel;
        }

        @Override
        protected Void doInBackground(String... activityName) {
            prepareListData(asyncTaskActivityViewModel.getActivityDescription(activityName[0]));
            return null;
        }
    }






    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.recyclerview_daily_activities, parent, false);
        return new ActivityViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ActivityViewHolder holder, int position)
    {
        if (activities != null)
        {
            final ActivityInstance current = activities.get(position);

            //Setup Description
            String activityName = current.getActivityInstanceName();

            descriptionListSetup(activityName);

            //Setup completion checkbox
            holder.completionCB.setText(current.getActivityInstanceName());
            if (current.getCompleted()) {holder.completionCB.setChecked(true);}
            else{holder.completionCB.setChecked(false);}

            holder.completionCB.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!holder.completionCB.isChecked()) { current.setCompleted(false); }
                    else { current.setCompleted(true); }
                    activityViewModel.updateActivityInstance(current);
                }
            });


            //Setup star checkbox
            if (current.getStar()) {holder.starCB.setChecked(true);}
            else{holder.starCB.setChecked(false);}

            holder.starCB.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!holder.starCB.isChecked()) { current.setStar(false); }
                    else { current.setStar(true); }
                    activityViewModel.updateActivityInstance(current);
                }
            });


            if (current.getAssociatedFile() != null) {holder.fileTV.setText(getFileName(Uri.parse(current.getAssociatedFile())));
                Log.i("zzz", "Text View set to: " + holder.fileTV.getText());
            }
            else{holder.fileTV.setText("Select a file to attach");}

            //Setup file selector TextView
            holder.fileTV.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    currentActivity = current;

                    if (current.getAssociatedFile() != null)
                    {
                        Intent openFile = new Intent();
                        openFile.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(current.getAssociatedFile());
                        openFile.setData(uri);
                        openFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        androidActivity.startActivity(openFile);
                    }
                    else {
                        performFileSearch();
                    }
                }
            });


            //Setup file remover Button
            holder.removeFileBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(current.getAssociatedFile() != null) {
                        Uri uri = Uri.parse(current.getAssociatedFile());
                        androidActivity.getApplicationContext().getContentResolver().delete(uri, null, null);
                        current.setAssociatedFile(null);
                    }
                    activityViewModel.updateActivityInstance(current);
                }
            });
        }

        else
        {
            // Covers the case of data not being ready yet.
            holder.completionCB.setText("No Activities");
        }

        setupExpandableView(); //Display description
    }


    public void setActivities(List<ActivityInstance> newActivities)
    {
        activities = newActivities;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    @Override
    public int getItemCount()
    {
        if (activities != null)
            return activities.size();
        else return 0;
    }





    //
    ActivityInstance currentActivity;
    public ActivityInstance getCurrentActivity(){return currentActivity;}

    public static Activity androidActivity;             //Made static to handle description population, may be dangerous
    public void setAndroidActivity(Activity newAndroidActivity){ androidActivity = newAndroidActivity;}


    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        androidActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }



    public String getFileName(Uri uri)
    {
        String fileName = "FileNameError";
        Cursor cursor = androidActivity.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                Log.i("zzz", "Retrieving string");
                fileName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        finally { if(cursor != null){cursor.close();} }

        Log.i("zzz", "Returning: " + fileName);
        return fileName;
    }
}

