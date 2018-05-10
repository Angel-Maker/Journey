package com.angelmaker.journey;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityRepository;

import java.util.List;

/**
 * Created by AngelPlayer on 5/7/2018.
 */

public class ActivityViewModel extends AndroidViewModel{
            private ActivityRepository repository;
            private LiveData<List<ActivityInstance>> allActivities;

            public ActivityViewModel (Application application) {
                super(application);
                repository = new ActivityRepository(application);
                allActivities = repository.getAllActivities();
            }

            LiveData<List<ActivityInstance>> getAllActivites() {return allActivities;}

            public void insert(ActivityInstance activityInstance) {repository.insert(activityInstance);}
            public void delete(ActivityInstance activityInstance) {repository.delete(activityInstance);}
}

