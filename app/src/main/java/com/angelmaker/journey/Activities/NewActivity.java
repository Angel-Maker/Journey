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
import com.angelmaker.journeyDatabase.ActivityType;

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

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

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
                            newActivityType(startDate, endDate);
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
                    try { selectedDate.setTime(sdfInput.parse(textView.getText().toString()));}
                    catch (ParseException e) { Log.e("zzz", "Failed to set initial spinner dates!"); }
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
                Log.i("zzz", "Date was set as: " + startDateTV.getText().toString());

                try
                {
                    startDate = sdfInput.parse(startDateTV.getText().toString());   //Returned date is of form yyyy/MM/dd
                    endDate = sdfInput.parse(endDateTV.getText().toString());
                }

                catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), "---ERROR PARSING DATE---\nPLEASE TRY AGAIN OR CONTACT THE DEVELOPER", Toast.LENGTH_LONG).show();
                    return false;
                }

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


    //newActivities

    //Creates a new activityType
    private void newActivityType(Date fromDate, Date toDate)
    {
        //Activity initialization variables
        String activityName = activityNameET.getText().toString();
        String activityDescription = descriptionET.getText().toString();
        String activityStartDateText = startDateTV.getText().toString();
        String activityEndDateText = endDateTV.getText().toString();

        String activityStartDate = changeDateDelimitation(activityStartDateText, "-");
        String activityEndDate = changeDateDelimitation(activityEndDateText, "-");

        //Add new activity type to database
        ActivityType newActivityType = new ActivityType();
        newActivityType.setActivityTypeName(activityName);
        newActivityType.setActivityDescription(activityDescription);
        newActivityType.setStartDate(activityStartDate);
        newActivityType.setEndDate(activityEndDate);

        activityViewModel.insertActivityType(newActivityType);

        newActivityInstance(activityName, fromDate, toDate);
    }

    //Changes format yyyy MM dd to use specified separating symbol
    private String changeDateDelimitation(String date, String symbol){
        String newDateFormat = date.substring(0,4) + symbol + date.substring(5,7) + symbol + date.substring(8,10);
        return newDateFormat;
    }

    //Creates new activity instances
    private void newActivityInstance(String activityName, Date fromDate, Date toDate)
    {
        //Create calendar objects of dates
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        startCalendar.setTime(fromDate);
        endCalendar.setTime(toDate);
        endCalendar.add(Calendar.DATE, 1); //Incremented by one so that day is included in the creation

        //Create String instances of Calendar objects
        String currentDate = sdfDB.format(startCalendar.getTime());
        String stopDate = sdfDB.format(endCalendar.getTime());

        //Insert new activity instances
        List<ActivityInstance> newActivityInstances = new ArrayList<>();

        //Loop to generate unique activity entries for every day in time frame window
        while (!currentDate.equals(stopDate))  //Comparison is done by string representation to ensure uniform formatting
        {
            ActivityInstance newActivity = new ActivityInstance();
            newActivity.setActivityInstanceName(activityName);
            newActivity.setCurrentDate(currentDate);

            newActivityInstances.add(newActivity);
            startCalendar.add(Calendar.DATE, 1);
            currentDate = sdfDB.format(startCalendar.getTime());
        }

        activityViewModel.insertActivityInstances(newActivityInstances);
    }






    private class updateActivityBtnClick extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... lists)
        {
            ActivityType activityType = activityViewModel.getFullActivityTypes(lists[0]);

            String oldName = activityType.getActivityTypeName();
            String oldDescription = activityType.getActivityDescription();
            String oldStartDateString = activityType.getStartDate();
            String oldEndDateString = activityType.getEndDate();
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

                //Update activity type information
                ActivityType updatedActivityType = new ActivityType();
                updatedActivityType.setActivityTypeName(newName);
                updatedActivityType.setActivityDescription(newDescription);
                updatedActivityType.setStartDate(newStartDateString);
                updatedActivityType.setEndDate(newEndDateString);
                activityViewModel.updateActivityType(activityType, updatedActivityType);

                //Update folder name
                if(!oldName.equals(newName)){
                    updateFolderName(oldName, newName);
                }

                List<ActivityInstance> activitiesToDelete = new ArrayList<ActivityInstance>();

                //If start date has been moved backwards then add new entries as needed
                if (oldStartDate.after(startDate)) { newStartDateActivities(newName, oldStartDate); }

                //If end date has been moved forward then add new entries as needed
                if (endDate.after(oldEndDate)) { newEndDateActivities(newName, oldEndDate); }


                List<ActivityInstance> activities = activityViewModel.getNamedActivityInstances(oldName);

                //Change existing activities
                for (int i = 0; i < activities.size(); i++)
                {
                    ActivityInstance activity = activities.get(i);                 //For each activity
                    Date activityCurrentDate = stringToDate(activity.getCurrentDate());

                    //If this activities record date is now outside the beginning of the date range, mark for deletion
                    if (startDate.after(activityCurrentDate)) { activitiesToDelete.add(activity); }

                    //If not deleted then if this activities record date is now outside the ending of the date range, mark for deletion
                    else if (activityCurrentDate.after(endDate)) { activitiesToDelete.add(activity); }
                }

                if (activitiesToDelete.size() != 0) { activityViewModel.deleteActivityInstances(activitiesToDelete, getApplicationContext()); }
            }

            newActivityInstance.finish();
            return null;
        }


        //Creates activities for changes in the start date
        private void newStartDateActivities(String activityName, Date oldStartDate){
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldStartDate);              //Get old start date to know when to stop adding days
            cal.add(Calendar.DATE, -1);         //Decrement by 1 to prevent duplicate entries for old start date
            Date insertionEndDate = cal.getTime();

            newActivityInstance(activityName, startDate, insertionEndDate); //Add activity entries from new start date to old start date (-1)
        }

        //Creates activities for changes in the end date
        private void newEndDateActivities(String activityName, Date oldEndDate){
            Calendar cal = Calendar.getInstance();
            cal.setTime(oldEndDate);                //Get old end date to know when to start adding days
            cal.add(Calendar.DATE, 1);          //Increment by 1 to prevent duplicate entries for old end date
            Date insertionStartDate = cal.getTime();

            newActivityInstance(activityName, insertionStartDate, endDate); //Add activity entries from old end date (+1) to new end date
        }


        private Date stringToDate(String dateString){
            Date date = null;

            try { date = sdfDB.parse(dateString); }
            catch (ParseException e) { Log.e("zzz", "Failed to set string to date!"); }

            return date;
        }

    }





    //Initialize fields to previous entries
    private class initializeFields extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(final String... lists) {
            ActivityType activityType = activityViewModel.getFullActivityTypes(lists[0]);

            activityNameET.setText(activityType.getActivityTypeName());
            descriptionET.setText(activityType.getActivityDescription());

            String startDateString = changeDateDelimitation(activityType.getStartDate(), "/");
            String endDateString = changeDateDelimitation(activityType.getEndDate(), "/");
            startDateTV.setText(startDateString);
            endDateTV.setText(endDateString);


            try {
                oldStartDate = sdfDB.parse(activityType.getStartDate());
                oldEndDate = sdfDB.parse(activityType.getEndDate());
            }
            catch (ParseException e) { Log.e("zzz", "Failed to set old dates! Date was: " + activityType.getStartDate());}

            return null;
        }
    }
}
