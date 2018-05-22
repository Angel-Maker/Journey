//todo - Add description text to DailyActivity view area

//todo - Add front screen help message if no activities have been added
//todo - Update main menu button to include number of activities done for that day
//todo - Track down reason for multiple dot icons in calendar after activity edits

//todo - Add "About App" menu item with info on app and contact information

//todo - Create "Journey View" that show progress over the time period

//todo - Clean up code base
//todo - Distribute beta test copies to friends for 7 days
//todo - Publish app


package com.angelmaker.journey;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.angelmaker.journey.ActivityViewModel;
import com.angelmaker.journey.DailyActivities;
import com.angelmaker.journey.R;
import com.angelmaker.journey.UpdateActivities;
import com.angelmaker.journeyDatabase.ActivityDao;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static boolean fileStorageFolderCreated = false;

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private SimpleDateFormat sdfCalendarTitle = new SimpleDateFormat("MMM YYYY", Locale.getDefault());
    private Calendar selectedDate;
    private Button dailyActivityBtn;
    private CompactCalendarView compactCalendar;
    private TextView timeWindowTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!fileStorageFolderCreated){
            createStorageFolder();
        }

        Toolbar mainMenuToolbar = findViewById(R.id.mainMenuToolbar);
        setSupportActionBar(mainMenuToolbar);

        selectedDate = Calendar.getInstance();

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
                String sdfDateClicked = sdfDB.format(dateClicked);
                dailyActivityBtn.setText(sdfDateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                timeWindowTV.setText(sdfCalendarTitle.format(firstDayOfNewMonth));
            }
        });
    }


    //Creates the folder that stores linked files
    private void createStorageFolder(){
        File folder = new File(getApplicationContext().getFilesDir() + "/linked_files");
        boolean success = true;
        if (!folder.exists()) { success = folder.mkdir(); }
        if (!success) {Log.i("zzz", "File could not be created");}
    }



    private void markStarredDays(){
        ActivityViewModel activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);

        //Find required dates to star
        activityViewModel.getUniqueStarred().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable final List<String> starredDates) {
                //Executed whenever the observed object changes
                for(int i = 0; i < starredDates.size(); i++) { addEvent(starredDates.get(i)); }   // Update the cached copy of the words in the adapter.
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
            startActivity(updateActivities);
        }
        return super.onOptionsItemSelected(item);
    }


    //View Objects
    public void dailyActivityBtn(View view) {
        Intent dailyActivities = new Intent(this, DailyActivities.class);

        dailyActivities.putExtra("EXTRA_CURRENT_DATE", selectedDate.get(Calendar.DATE));
        dailyActivities.putExtra("EXTRA_CURRENT_MONTH", selectedDate.get(Calendar.MONTH));

        startActivity(dailyActivities);
    }
}

