package com.angelmaker.journey;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.angelmaker.journeyDatabase.ActivityInstance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewActivity extends AppCompatActivity {

    EditText activityNameET;

    private ActivityViewModel activityViewModel;

    private SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        activityViewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);

        activityNameET = findViewById(R.id.activityNameET);


        final Button button = findViewById(R.id.SubmitBtn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (TextUtils.isEmpty(activityNameET.getText())) {}
                else {
                    String activityName = activityNameET.getText().toString();
                    String activityDescription = "No Description";
                    String activityStartDate = "2018/05/10";
                    String activityEndDate = "2018/05/19";

                    int day = 0;
                    List<ActivityInstance> newFullActivity = new ArrayList<ActivityInstance>();
                    while (day < 10)
                    {
                        ActivityInstance newActivity = new ActivityInstance();
                        newActivity.setActivityName(activityName);
                        newActivity.setActivityDescription(activityDescription);
                        newActivity.setStartDate(activityStartDate);
                        newActivity.setEndDate(activityEndDate);
                        newActivity.setCurrentDate("2018/05/1" + Integer.toString(day));

                        newFullActivity.add(newActivity);

                        day++;
                    }

                    activityViewModel.insertMany(newFullActivity);
                }

                finish();
            }
        });
    }
}
