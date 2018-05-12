package com.angelmaker.journey;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewActivity extends AppCompatActivity {

    EditText activityNameET;

    private ActivityViewModel activityViewModel;
    private EditText DescriptionET;
    private TextView startDateTV;
    private TextView endDateTV;

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);

        activityNameET = findViewById(R.id.activityNameET);
        startDateTV = findViewById(R.id.startDateTV);
        endDateTV = findViewById(R.id.endDateTV);
        DescriptionET = findViewById(R.id.DescriptionET);

        linkDatePickerFragment(startDateTV);
        linkDatePickerFragment(endDateTV);
    }



    public void linkDatePickerFragment(final TextView textView){
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setTV(textView);
                datePicker.show(getFragmentManager(), "DatePicker");
            }
        });
    }



    public void SubmitBtnClicked (View view)
    {
        if (!TextUtils.isEmpty(activityNameET.getText()))
        {
            if (!TextUtils.isEmpty(startDateTV.getText()) && !TextUtils.isEmpty(endDateTV.getText()))
            {
                //Activity initialization variables
                String activityName = activityNameET.getText().toString();
                String activityDescription = DescriptionET.getText().toString();
                String activityStartDate = startDateTV.getText().toString();
                String activityEndDate = endDateTV.getText().toString();

                //Create Date objects from String data
                Date startDate = null;
                Date endDate = null;

                try
                {
                    startDate = sdfDB.parse(activityStartDate);
                    endDate = sdfDB.parse(activityEndDate);
                }
                catch (ParseException e) {e.printStackTrace();}

                //Check that end date is equal or after start date
                if(endDate.equals(startDate) || endDate.after(startDate)){
                    //Create calendar objects of dates
                    Calendar startCalendar = Calendar.getInstance();
                    Calendar endCalendar = Calendar.getInstance();

                    startCalendar.setTime(startDate);
                    endCalendar.setTime(endDate);

                    //Create String instances of Calendar objects
                    String currentDate = sdfDB.format(startCalendar.getTime());
                    endCalendar.add(Calendar.DATE, 1);
                    String stopDate = sdfDB.format(endCalendar.getTime());
                    endCalendar.add(Calendar.DATE, -1);

                    List<ActivityInstance> newFullActivity = new ArrayList<>();

                    //Loop to generate unique activity entries for every day in time frame window
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

                    finish();
                }
                else{Toast.makeText(getApplicationContext(), "The end date must be the same or come after the start date", Toast.LENGTH_LONG).show();}
            }
            else{Toast.makeText(getApplicationContext(), "Please enter a start and end date", Toast.LENGTH_LONG).show();}
        }
        else{ Toast.makeText(getApplicationContext(), "Please enter an activity name", Toast.LENGTH_LONG).show(); }
    }
}
