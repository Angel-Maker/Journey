package com.angelmaker.journey;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
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

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */


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
                //File selectedFile = new File("data/media/0/Download/cat.jpg");

                ActivityInstance changedActivity = adapter.getCurrentActivity();

                String shortName = adapter.getFileName(uri);
                File targetLocation = new File(getApplicationContext().getFilesDir() + "/linked_files/"
                        + changedActivity.getActivityName() + "/"
                        + sdfFile.format(selectedDate.getTime()) + ": " + shortName);

                //Copy file to app storage
                try { copy(uri, targetLocation); }
                catch (IOException e) {
                    e.printStackTrace();
                    Log.i("zzz","Error copying file");
                }

                //Update DB URI entry
                changedActivity.setAssociatedFile(uri.toString());
                activityViewModel.update(changedActivity);
            }
        }
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    public void dumpImageMetaData(Context context, Uri uri) {

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("zzz", "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i("zzz", "Size: " + size);
            }
        } finally {
            cursor.close();
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

