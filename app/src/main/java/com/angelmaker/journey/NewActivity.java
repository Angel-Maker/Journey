package com.angelmaker.journey;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewActivity extends AppCompatActivity {

    EditText activityNameET;

    private ActivityViewModel activityViewModel;

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
                if (TextUtils.isEmpty(activityNameET.getText())) {
                } else {
                    String activityName = activityNameET.getText().toString();

                    ActivityInstance newActivity = new ActivityInstance(activityName);
                    activityViewModel.insert(newActivity);
                }

                finish();
            }
        });
    }
}
