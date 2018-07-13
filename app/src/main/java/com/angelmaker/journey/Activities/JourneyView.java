package com.angelmaker.journey.Activities;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.DailyActivitiesListAdapter;
import com.angelmaker.journey.supportFiles.FinishedJourneysListAdapter;
import com.angelmaker.journey.supportFiles.JourneyViewListAdapter;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JourneyView extends AppCompatActivity {

    private ActivityViewModel activityViewModel;
    private TextView journeyTitleTV;
    private Button saveBtn;
    JourneyViewListAdapter adapter;

    String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_view);

        journeyTitleTV = findViewById(R.id.journeyTitleTV);
        saveBtn = findViewById(R.id.saveBtn);

        activityName = getIntent().getStringExtra("EXTRA_ACTIVITY_NAME");
        journeyTitleTV.setText("Your Journey for: " + activityName);

        //Initialize view model
        activityViewModel = new ActivityViewModel(getApplication());

        setRecyclerView();
        new populateList().execute(activityName);
        addSaveBtnClick();
    }

    private void setRecyclerView()
    {
        //RecyclerView Setup
        RecyclerView recyclerView = findViewById(R.id.journeyViewRV);
        adapter = new JourneyViewListAdapter(this);
        adapter.setAndroidActivity(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add dividers between cells
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }


    private void addSaveBtnClick(){
        final Activity currentActivity = this;

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Get Journey files directory
                final String originalFilePath = getApplicationContext().getFilesDir() + "/linked_files/" + activityName;
                File originalFile = new File(originalFilePath);

                //Get Downloads file directory
                final String targetFileFolderPath = "/storage/self/primary/Download/" + activityName;
                //final String targetFileFolderPath = getApplicationContext().getFilesDir() + "/linked_files/ddd";
                File targetFolder = new File(targetFileFolderPath);
                targetFolder.mkdir();

                for (File child : originalFile.listFiles())
                {
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.angelmaker.journey", child);
                    String fileName = DailyActivitiesListAdapter.getFileName(uri, currentActivity);

                    try
                    {
                        final String targetFilePath = "/storage/self/primary/Download/" + activityName +"/"+ fileName;
                        File targetFile = new File(targetFilePath);
                        copy(uri, targetFile);
                        Log.i("zzz", "File copied");
                    }

                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(JourneyView.this, "Your Journey was saved to the downloads folder!", Toast.LENGTH_LONG).show();
            }
        });
    }




    //Sets button text to selected date and number of completed activities
    private class populateList extends AsyncTask<String, Void, List<ActivityInstance>> {
        @Override
        protected List<ActivityInstance> doInBackground(final String... lists)
        {
            List<ActivityInstance> allActivities = activityViewModel.getNamedActivityInstances(lists[0]);
            List<ActivityInstance> newActivities = new ArrayList<>();

            for (int i = 1 ; i < allActivities.size() ; i++){
                ActivityInstance activity = allActivities.get(i);

                if(activity.getAssociatedFile() != null)
                {
                    newActivities.add(activity);
                }
            }

            return newActivities;
        }

        @Override
        protected void onPostExecute(List<ActivityInstance> newActivities)
        {
            //Pass in data to build list around
            adapter.setActivities(newActivities);
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
