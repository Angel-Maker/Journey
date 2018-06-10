package com.angelmaker.journey.Activities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.DatePickerFragment;
import com.angelmaker.journeyDatabase.ActivityInstance;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewActivity extends AppCompatActivity {
    private Activity newActivityInstance;

    private EditText activityNameET;
    private ActivityViewModel activityViewModel;
    private EditText descriptionET;
    private TextView startDateTV;
    private TextView endDateTV;
    private Button submitBtn;

    private Date startDate = null;
    private Date endDate = null;
    private Date oldStartDate = null;
    private Date oldEndDate = null;

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        newActivityInstance = this;

        activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);

        activityNameET = findViewById(R.id.activityNameET);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        descriptionET = findViewById(R.id.descriptionET);
        submitBtn = findViewById(R.id.submitBtn);

        linkDatePickerFragment(startDateTV);
        linkDatePickerFragment(endDateTV);

        //Updating activity
        if(getIntent().getStringExtra("activityName") != null)
        {
            final String activityName = getIntent().getStringExtra("activityName");
            new initializeFields().execute(activityName);

            submitBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(validInput()){

                        //Warn user and allow back-out if days will be deleted and will result in data loss
                        if (startDate.after(oldStartDate) || oldEndDate.after(endDate))
                        {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { builder = new AlertDialog.Builder(newActivityInstance, android.R.style.Theme_Material_Dialog_Alert); }
                            else { builder = new AlertDialog.Builder(newActivityInstance); }

                            builder.setTitle("Entry deletion warning!")
                                    .setMessage("You have changed the start or end date such that some activity records are no longer in your date range and will be deleted.\n\nDo you want to proceed?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new updateActivityBtnClick().execute(activityName);
                                            Toast.makeText(getApplicationContext(), "Activity updated", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        }

                        else{
                            new updateActivityBtnClick().execute(activityName);
                            Toast.makeText(getApplicationContext(), "Activity updated", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }

        //Creating a new activity
        else{
            submitBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(validInput())
                    {
                        Boolean folderCreated = createFileFolder(activityNameET.getText().toString());

                        if(folderCreated) {
                            newActivities(startDate, endDate);
                            finish();
                        }

                        else{
                            Toast.makeText(getApplicationContext(),
                                    "---This activity already exists---\nUse the previous page if you would like to edit it.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }



    //Creates folder for a specific activity
    private boolean createFileFolder(String activityName){
        File folder = new File(getApplicationContext().getFilesDir()+ "/linked_files/" + activityName);
        boolean success = false;

        if (!folder.exists()) { success = folder.mkdir(); }
        return success;
    }

    //Changes folder name for an activity
    private void updateFolderName(String oldName, String newName){
        File oldFolder = new File(getApplicationContext().getFilesDir()+ "/linked_files/" + oldName);
        File newFolder = new File(getApplicationContext().getFilesDir()+ "/linked_files/" + newName);
        boolean success = true;

        if (oldFolder.exists()) { success = oldFolder.renameTo(newFolder); }
        else {Log.i("zzz", "File to be updated does not exist");}
        if (!success) {Log.i("zzz", "File could not be updated");}
    }




    private void linkDatePickerFragment(final TextView textView){
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setTV(textView);

                Calendar selectedDate = Calendar.getInstance();

                //If a date has already been set
                if(!textView.getText().toString().equals(""))
                {
                    //Set spinner to start on the selected date
                    try { selectedDate.setTime(sdfDB.parse(textView.getText().toString()));}
                    catch (ParseException e) { e.printStackTrace(); }
                }

                datePicker.setSpinnerDate(selectedDate);
                datePicker.show(getFragmentManager(), "DatePicker");
            }
        });
    }


    private boolean validInput()
    {
        Boolean valid = false;

        //Check activity has a name
        if (!TextUtils.isEmpty(activityNameET.getText()))
        {
            //Check activity has a start and end dates
            if (!TextUtils.isEmpty(startDateTV.getText()) && !TextUtils.isEmpty(endDateTV.getText()))
            {
                try
                {
                    startDate = sdfDB.parse(startDateTV.getText().toString());
                    endDate = sdfDB.parse(endDateTV.getText().toString());
                }

                catch (ParseException e) {e.printStackTrace();}

                //Check that end date is equal or after start date
                if(endDate.equals(startDate) || endDate.after(startDate))
                {
                    valid = true;
                }

                else{Toast.makeText(getApplicationContext(), "The end date must be the same or come after the start date", Toast.LENGTH_LONG).show();}
            }
            else{Toast.makeText(getApplicationContext(), "Please enter a start and end date", Toast.LENGTH_LONG).show();}
        }
        else{ Toast.makeText(getApplicationContext(), "Please enter a name for your activity", Toast.LENGTH_LONG).show(); }

        return valid;
    }

    //Creates new activities between two dates
    private void newActivities(Date fromDate, Date toDate)
    {
        //Activity initialization variables
        String activityName = activityNameET.getText().toString();
        String activityDescription = descriptionET.getText().toString();
        String activityStartDate = startDateTV.getText().toString();
        String activityEndDate = endDateTV.getText().toString();

        //Create calendar objects of dates
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTime(fromDate);
        endCalendar.setTime(toDate);

        //Create String instances of Calendar objects
        String currentDate = sdfDB.format(startCalendar.getTime());
        endCalendar.add(Calendar.DATE, 1);
        String stopDate = sdfDB.format(endCalendar.getTime());
        endCalendar.add(Calendar.DATE, -1);

        List<ActivityInstance> newFullActivity = new ArrayList<>();

        //Loop to generate unique androidActivity entries for every day in time frame window
        while (!currentDate.equals(stopDate))
        {
            ActivityInstance newActivity = new ActivityInstance();
            newActivity.setActivityName(activityName);
            newActivity.setActivityDescription(activityDescription);
            newActivity.setStartDate(activityStartDate);
            newActivity.setEndDate(activityEndDate);
            newActivity.setCurrentDate(currentDate);

            newFullActivity.add(newActivity);
            startCalendar.add(Calendar.DATE, 1);
            currentDate = sdfDB.format(startCalendar.getTime());
        }

        activityViewModel.insertMany(newFullActivity);
    }






    private class updateActivityBtnClick extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... lists)
        {
            List<ActivityInstance> fullActivity = activityViewModel.getFullActivity(lists[0]);
            ActivityInstance activity = fullActivity.get(0);

            String oldName = activity.getActivityName();
            String oldDescription = activity.getActivityDescription();
            String oldStartDateString = activity.getStartDate();
            String oldEndDateString = activity.getEndDate();
            // oldStartDate (initialized elsewhere) contains the old start date in a Date variable
            // oldEndDate (initialized elsewhere) contains the old end date in a Date variable


            String newName = activityNameET.getText().toString();
            String newDescription = descriptionET.getText().toString();
            String newStartDateString = sdfDB.format(startDate);
            String newEndDateString = sdfDB.format(endDate);
            //startDate (initialized elsewhere) contains the new start date in a Date variable
            //endDate (initialized elsewhere) contains the new end date in a Date variable

            //If any attributes have changed, delete, create, and update entries as needed.
            if(!oldName.equals(newName) ||
                    !oldDescription.equals(newDescription) ||
                    !newStartDateString.equals(oldStartDateString) ||
                    !newEndDateString.equals(oldEndDateString))
            {
                List<ActivityInstance> activitiesToDelete = new ArrayList<ActivityInstance>();
                List<ActivityInstance> activitiesToAdd = new ArrayList<ActivityInstance>();
                List<ActivityInstance> activitiesToUpdate = new ArrayList<ActivityInstance>();



                //If start date has been moved backwards then add new entries as needed
                if (oldStartDate.after(startDate)) { newStartDateActivities(oldStartDate); }

                //If end date has been moved forward then add new entries as needed
                if (endDate.after(oldEndDate)) { newEndDateActivities(oldEndDate); }

                if(!oldName.equals(newName)){
                    updateFolderName(oldName, newName);
                }

                //Change existing activities
                for (int i = 0; i < fullActivity.size(); i++)
                {
                    activity = fullActivity.get(i);                 //For each activity
                    Date activityCurrentDate = stringToDate(activity.getCurrentDate());

                    //If this activities record date is now outside the beginning of the date range, mark for deletion
                    if (startDate.after(activityCurrentDate)) { activitiesToDelete.add(activity); }

                    //If not deleted then if this activities record date is now outside the ending of the date range, mark for deletion
                    else if (activityCurrentDate.after(endDate)) { activitiesToDelete.add(activity); }

                    //If still not deleted, update all fields
                    else {
                        activity.setActivityName(newName);
                        activity.setActivityDescription(newDescription);
                        activity.setStartDate(newStartDateString);
                        activity.setEndDate(newEndDateString);

                        activitiesToUpdate.add(activity);               //Add activity to be updated in DB
                    }
                }

                if (activitiesToDelete.size() != 0) { activityViewModel.deleteMany(activitiesToDelete, getApplicationContext()); }
                if (activitiesToUpdate.size() != 0){ activityViewModel.updateMany(activitiesToUpdate);}
                if (activitiesToAdd.size() != 0) { activityViewModel.insertMany(activitiesToAdd); }

                newActivityInstance.finish();
            }
            return null;
        }


        //Creates activities for changes in the start date
        private void newStartDateActivities(Date oldStartDate){
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldStartDate);              //Get old start date to know when to stop adding days
            cal.add(Calendar.DATE, -1);         //Decrement by 1 to prevent duplicate entries for old start date
            Date insertionEndDate = cal.getTime();

            newActivities(startDate, insertionEndDate); //Add activity entries from new start date to old start date (-1)
        }

        //Creates activities for changes in the end date
        private void newEndDateActivities(Date oldEndDate){
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldEndDate);                //Get old end date to know when to start adding days
            cal.add(Calendar.DATE, 1);          //Increment by 1 to prevent duplicate entries for old end date
            Date insertionStartDate = cal.getTime();

            newActivities(insertionStartDate, endDate); //Add activity entries from old end date (+1) to new end date
        }


        private Date stringToDate(String dateString){
            Date date = null;

            try { date = sdfDB.parse(dateString); }
            catch (ParseException e) { e.printStackTrace(); }

            return date;
        }

    }





    //Initialize fields to previous entries
    private class initializeFields extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... lists) {
            List<ActivityInstance> fullActivity = activityViewModel.getFullActivity(lists[0]);
            ActivityInstance activity = fullActivity.get(0);

            activityNameET.setText(activity.getActivityName());
            startDateTV.setText(activity.getStartDate());
            endDateTV.setText(activity.getEndDate());
            descriptionET.setText(activity.getActivityDescription());

            try {
                oldStartDate = sdfDB.parse(activity.getStartDate());
                oldEndDate = sdfDB.parse(activity.getEndDate());
            }
            catch (ParseException e) { e.printStackTrace(); }
            return null;
        }
    }
}