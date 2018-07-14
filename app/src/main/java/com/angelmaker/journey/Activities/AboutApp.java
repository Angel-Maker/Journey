package com.angelmaker.journey.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.angelmaker.journey.R;
import com.angelmaker.journey.supportFiles.DailyActivitiesExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutApp extends AppCompatActivity {

    private ExpandableListView expListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        expListView = findViewById(R.id.aboutAppETV);
        setupExpandableView();
    }


    public void setupExpandableView() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add(getString(R.string.about_app_what_is_journey_title));
        listDataHeader.add(getString(R.string.about_app_concept_title));
        listDataHeader.add(getString(R.string.about_app_getting_started_title));
        listDataHeader.add(getString(R.string.about_app_recommendations_title));
        listDataHeader.add(getString(R.string.about_app_developer_title));
        listDataHeader.add(getString(R.string.about_app_feedback_title));


        // Adding header data
        List<String> what_is_journey = new ArrayList<>();
        what_is_journey.add(getString(R.string.about_app_what_is_journey_content));

        List<String> concept = new ArrayList<>();
        concept.add(getString(R.string.about_app_concept_content));

        List<String> getting_started = new ArrayList<>();
        getting_started.add(getString(R.string.about_app_getting_started_content));

        List<String> recommendations = new ArrayList<>();
        recommendations.add(getString(R.string.about_app_recommendations_content));

        List<String> developer = new ArrayList<>();
        developer.add(getString(R.string.about_app_developer_content));

        List<String> feedback = new ArrayList<>();
        feedback.add(getString(R.string.about_app_feedback_content));



        listDataChild.put(listDataHeader.get(0), what_is_journey);
        listDataChild.put(listDataHeader.get(1), concept);
        listDataChild.put(listDataHeader.get(2), getting_started);
        listDataChild.put(listDataHeader.get(3), recommendations);
        listDataChild.put(listDataHeader.get(4), developer);
        listDataChild.put(listDataHeader.get(5), feedback);


        listAdapter = new DailyActivitiesExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild, 1);
        expListView.setAdapter(listAdapter);

    }
}
