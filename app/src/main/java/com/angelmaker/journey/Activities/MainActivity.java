//todo - Edit "About App"
//todo - Fix Details display in daily activity
//todo - Adjust what is defaulted to load in main screen (no activities message sometimes shows up for a moment)
//todo - (time permitting) Make daily activity view transition properly
//todo - (time permitting) Make main display show multiple dots if multiple stared activities
//todo - (time permitting) Add notes to daily activities
//todo - (time permitting) Create finishing presentation of Journey based on medium selected.
//todo - (time permitting) Allow selective files to be saved to downloads after finishing a Journey instead of all files

//todo - Clean up code
//todo - Distribute beta test copies
//todo - Publish app


package com.angelmaker.journey.Activities;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.angelmaker.journey.R;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static boolean fileStorageFolderCreated = false;

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfCalendarTitle = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private Calendar selectedDate;
    private Button dailyActivityBtn;
    private CompactCalendarView compactCalendar;
    private TextView timeWindowTV;
    private ActivityViewModel activityViewModel;
    private TextView noActivitiesTV;
    private Button completedJourneyBtn;
    private ArrayList<ActivityType> finishedActivities;

    static final int BACK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!fileStorageFolderCreated){ createStorageFolder(); }

        noActivitiesTV = findViewById(R.id.noActivitiesTV);
        completedJourneyBtn = findViewById(R.id.completedJourneyBtn);

        Toolbar mainMenuToolbar = findViewById(R.id.mainMenuToolbar);
        setSupportActionBar(mainMenuToolbar);

        selectedDate = Calendar.getInstance();
        activityViewModel = new ActivityViewModel(getApplication());

        startNoticeBar();

        dailyActivityBtn = findViewById(R.id.dailyActivityBtn);
        dailyActivityBtn.setText(sdfDB.format(selectedDate.getTime()));

        //Calendar setup
        timeWindowTV = findViewById(R.id.timeWindowTV);
        timeWindowTV.setText(sdfCalendarTitle.format(selectedDate.getTime()));
        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        markStarredDays();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate.setTime(dateClicked);
                new updateButtonText().execute();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                timeWindowTV.setText(sdfCalendarTitle.format(firstDayOfNewMonth));
            }
        });

        new updateButtonText().execute();
        new setVisibilityOfFJB().execute();
        checkPermissions();
    }

    public void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        }
    }



    //Creates the folder that stores linked files
    private void createStorageFolder(){
        File folder = new File(getApplicationContext().getFilesDir() + "/linked_files");
        boolean success = true;
        if (!folder.exists()) { success = folder.mkdir(); }
        if (!success) {Log.i("zzz", "File could not be created");}
    }



    //Calendar Settings
    private void markStarredDays(){
        ActivityViewModel activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);

        //Find required dates to star
        activityViewModel.getUniqueStarred().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> starredDates) {
                //Executed whenever the observed object changes
                compactCalendar.removeAllEvents();
                for(int i = 0; i < starredDates.size(); i++) { addEvent(starredDates.get(i)); }
            }
        });
    }

    public void addEvent(String starredDate){
        long epochDate = 0;
        try { epochDate = sdfDB.parse(starredDate).getTime(); }
        catch (ParseException e) { e.printStackTrace(); }

        Event ev1 = new Event(Color.rgb(235,235,0), epochDate, "Starred");
        compactCalendar.addEvent(ev1);
    }



    //Menu bar code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mainMenuInflater = getMenuInflater();
        mainMenuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.updateActivitiesMenuItem){
            Intent updateActivities = new Intent(this, UpdateActivities.class);
            startActivityForResult(updateActivities, BACK_REQUEST);
        }
        if(item.getItemId() == R.id.aboutAppMenuItem){
            Intent aboutApp = new Intent(this, AboutApp.class);
            startActivity(aboutApp);
        }
        return super.onOptionsItemSelected(item);
    }


    //View Objects
    private void startNoticeBar(){
        //Link view model to database
        activityViewModel.getActivityNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> activities) {
                //Set visibility of no activity help message
                if(activities.size() == 0){noActivitiesTV.setVisibility(View.VISIBLE);}
                else{noActivitiesTV.setVisibility(View.INVISIBLE);}
            }
        });
    }


    public void completedJourneyBtn(View view) {
        Intent journeyView = new Intent(this, FinishedJourneys.class);

        journeyView.putExtra("EXTRA_FINISHED_ACTIVITIES", finishedActivities);
        startActivityForResult(journeyView, BACK_REQUEST);
    }

    public void dailyActivityBtn(View view) {
        Intent dailyActivities = new Intent(this, DailyActivities.class);

        dailyActivities.putExtra("EXTRA_CURRENT_DATE", selectedDate.get(Calendar.DATE));
        dailyActivities.putExtra("EXTRA_CURRENT_MONTH", selectedDate.get(Calendar.MONTH));

        startActivityForResult(dailyActivities, BACK_REQUEST);
    }



    //Sets button text to selected date and number of completed activities
    private class updateButtonText extends AsyncTask<String, Void, List<ActivityInstance>> {
        @Override
        protected List<ActivityInstance> doInBackground(final String... lists) {
            //Update date selected button
            Log.i("zzz", "Change in DailyActivities");

            List<ActivityInstance> activities = activityViewModel.getSpecifiedDailyActivities(sdfDB.format(selectedDate.getTime()));


            return activities;
        }

        @Override
        protected void onPostExecute(List<ActivityInstance> activities) {
            //Executed whenever the observed object changes
            int completedActivities = 0;
            Boolean stared = false;
            ActivityInstance activity;


            //Check each activity and tally the number of completed activities
            for (int i = 0; i < activities.size(); i++) {
                activity = activities.get(i);
                if (activity.getCompleted()) {
                    completedActivities++;
                }
                if (activity.getStar()) {
                    stared = true;
                }
            }

            String selectedDateString = sdfDB.format(selectedDate.getTime());
            String displayText = selectedDateString + "\nCompleated: " + completedActivities + "/" + activities.size();
            dailyActivityBtn.setText(displayText);

            if (stared) {
                dailyActivityBtn.setBackgroundColor(0xFFFFDF00);
            }    // 0xAARRGGBB    "Golden Yellow"
            else {
                dailyActivityBtn.setBackgroundColor(0xFFC0C0C0);
            }          // 0xAARRGGBB    "Silver"
        }
    }




    //Update visibility of finished Journey button
    private class setVisibilityOfFJB extends AsyncTask<String, Void, ArrayList<ActivityType>> {
        @Override
        protected ArrayList<ActivityType> doInBackground(final String... lists)
        {
            //Update completed Journey button
            Calendar todayCal = Calendar.getInstance();
            String todayString = sdfDB.format(todayCal.getTime());

            ArrayList<ActivityType> finishedActivities = activityViewModel.getFinishedActivityTypes(todayString);

            return finishedActivities;
        }

        @Override
        protected void onPostExecute(ArrayList<ActivityType> newFinishedActivities) {
            finishedActivities = newFinishedActivities;

            //Set visibility of FinishedJourneyBtn
            if(finishedActivities.size() != 0){
                completedJourneyBtn.setVisibility(View.VISIBLE);
            }
            else{
                completedJourneyBtn.setVisibility(View.INVISIBLE);
            }
        }
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When activities return to main by back button, update button and calendar
        if (requestCode == BACK_REQUEST) {
            new updateButtonText().execute();
        }
    }
}

