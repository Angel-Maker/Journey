package com.angelmaker.journey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private final int PERMISSIONS_REQUEST_MANAGE_DOCUMENTS = 2048;

    private ActivityViewModel activityViewModel;

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

            //Setup completion checkbox
            holder.completionCB.setText(current.getActivityName());
            if (current.getCompleted()) {holder.completionCB.setChecked(true);}
            else{holder.completionCB.setChecked(false);}

            holder.completionCB.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!holder.completionCB.isChecked()) { current.setCompleted(false); }
                    else { current.setCompleted(true); }
                    activityViewModel.update(current);
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
                    activityViewModel.update(current);
                }
            });


            if (current.getAssociatedFile() != null) {holder.fileTV.setText(getFileName(Uri.parse(current.getAssociatedFile())));}
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
                    current.setAssociatedFile(null);
                    activityViewModel.update(current);
                }
            });


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





    //
    ActivityInstance currentActivity;
    public ActivityInstance getCurrentActivity(){return currentActivity;}

    public Activity androidActivity;
    public void setAndroidActivity(Activity newAndroidActivity){ androidActivity = newAndroidActivity;}


    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");

        Log.i("zzz", "androidActivity start");
        androidActivity.startActivityForResult(intent, READ_REQUEST_CODE);
    }






    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = androidActivity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        //If no name is returned, return last part of URI
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}




























/*
                        Code to ask permissions
                        Originally believed to be needed to open files but turned out to be unnecessary
                        Leaving code here in the event it is needed for further file manipulation

                        if (ContextCompat.checkSelfPermission(androidActivity.getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            // Permission is not granted
                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(androidActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE))
                            {
                                // Show an explanation to the user
                                Toast.makeText(androidActivity, "Permission needed to open your attached file", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions(androidActivity,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_REQUEST_MANAGE_DOCUMENTS);
                            }
                        }

                        else {
                            Intent openFile = new Intent();
                            openFile.setAction(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse(current.getAssociatedFile());
                            openFile.setData(uri);
                            openFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            androidActivity.startActivity(openFile);
                        }*/