package com.angelmaker.journey;


import android.app.Activity;
import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.UserDictionary;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DailyActivities extends AppCompatActivity {

    //Variables to record current date being accessed
    private Calendar selectedDate;
    private SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private SimpleDateFormat sdfFile = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    //Gesture for day swipe transition
    private GestureDetectorCompat gestureObject;
    private DailyActivitiesListAdapter adapter = null;

    //Variable to access database
    private ActivityViewModel activityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_activities);

        processDate();
        setActionBar(sdfDisplay.format(selectedDate.getTime()));
        setRecyclerView();

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());
    }


    private void processDate(){
        //Retrieve passed date information
        selectedDate = Calendar.getInstance();
        selectedDate.set(Calendar.DATE, getIntent().getIntExtra("EXTRA_CURRENT_DATE", 1));
        selectedDate.set(Calendar.MONTH, getIntent().getIntExtra("EXTRA_CURRENT_MONTH", 1));
    }


    private void setActionBar(String stringDate){
        //Create action bar
        Toolbar dailyActivitiesToolbar = findViewById(R.id.dailyActivitiesToolbar);
        setSupportActionBar(dailyActivitiesToolbar);

        //Customize action bar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //TODO- Find better way to center text
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if(rotation == 0 || rotation == 2){ ab.setTitle("                "+stringDate);}
        else { ab.setTitle("                                                                "+stringDate); }
    }


    private void setRecyclerView()
    {
        //Initialize view model
        activityViewModel = new ActivityViewModel(getApplication(), sdfDB.format(selectedDate.getTime()));

        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.dailyActivityRV);
        adapter = new DailyActivitiesListAdapter(this);
        adapter.setViewModel(activityViewModel);
        adapter.setAndroidActivity(DailyActivities.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        //Link view model to database
        activityViewModel.getDailyActivities().observe(this, new Observer<List<ActivityInstance>>() {
            @Override
            public void onChanged(@Nullable final List<ActivityInstance> activities) {
                //Executed whenever the observed object changes
                adapter.setActivities(activities);   // Update the cached copy of the words in the adapter.
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    class LearnGesture extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY){
            if(event2.getX() > event1.getX()){
                selectedDate.add(Calendar.DATE, -1);
                changeDate(0);
            }

            else if (event2.getX() < event1.getX()){
                selectedDate.add(Calendar.DATE, 1);
                changeDate(1);
            }
            return true;
        }
    }


    //Creates an new androidActivity for the new day (0 = one day ago, 1 = tomorrow)
    public void changeDate(int direction)
    {
        Intent dailyActivities = new Intent(this, DailyActivities.class);

        dailyActivities.putExtra("EXTRA_CURRENT_DATE", selectedDate.get(Calendar.DATE));
        dailyActivities.putExtra("EXTRA_CURRENT_MONTH", selectedDate.get(Calendar.MONTH));

        finish();

        if(direction == 0){overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);}
        else if(direction == 1){overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);}

        startActivity(dailyActivities);
    }




    private static final int READ_REQUEST_CODE = 42;
    private static final int WRITE_REQUEST_CODE = 43;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */


    private Uri targatedUri;
    private Uri newLocationUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                //Get file information
                Uri uri = resultData.getData();
                ActivityInstance changedActivity = adapter.getCurrentActivity();

                String shortName = adapter.getFileName(uri);
                final String filepath = getApplicationContext().getFilesDir() + "/linked_files/"
                        + changedActivity.getActivityName() + "/"
                        + sdfFile.format(selectedDate.getTime()) + ": " + shortName;

                File targetLocation = new File(filepath);

                //Copy file to app storage
                try {
                    copy(uri, targetLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("zzz", "Error copying file");
                }

                Uri newUri = FileProvider.getUriForFile(getApplicationContext(), "com.angelmaker.journey", targetLocation);

                Log.i("zzz", "newURI: " + newUri.toString());


                //Update DB URI entry
                changedActivity.setAssociatedFile(newUri.toString());
                activityViewModel.update(changedActivity);
            }
        }
    }






    private void copy(Uri uri, File dst) throws IOException {
        InputStream in = getContentResolver().openInputStream(uri);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
