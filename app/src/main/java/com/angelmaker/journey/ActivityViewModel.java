package com.angelmaker.journey;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityRepository;

import java.util.Calendar;
import java.util.List;

/**
 * Created by AngelPlayer on 5/7/2018.
 */

public class ActivityViewModel extends AndroidViewModel{
    private ActivityRepository repository;
    private LiveData<List<String>> activityNames;
    private LiveData<List<ActivityInstance>> dailyActivities;
    private LiveData<List<String>> uniqueStarred;



    public ActivityViewModel (Application application) {
        super(application);
        repository = new ActivityRepository(application);
        activityNames = repository.getActivityNames();
        uniqueStarred = repository.getUniqueStarred();
    }

    public ActivityViewModel (Application application, String viewedDate) {
        super(application);
        repository = new ActivityRepository(application, viewedDate);
        dailyActivities = repository.getDaysActivities();
    }


    LiveData<List<String>> getUniqueStarred() {return uniqueStarred;}
    LiveData<List<String>> getActivityNames() {return activityNames;}
    LiveData<List<ActivityInstance>> getDailyActivities() {return dailyActivities;}


    public List<ActivityInstance> getFullActivity(String getActivityName) { return repository.getFullActivity(getActivityName); }
    public void insertMany(List<ActivityInstance> activityInstances) {repository.insertMany(activityInstances);}
    public void update (ActivityInstance activityInstance) {repository.update(activityInstance);}
    public void updateMany(List<ActivityInstance> activityInstances) {repository.updateMany(activityInstances);}
    public void deleteMany(List<ActivityInstance> activityInstances) {repository.deleteMany(activityInstances);}
    public void deleteFullActivity(String removeActivityName) {repository.deleteFullActivity(removeActivityName); }
}

