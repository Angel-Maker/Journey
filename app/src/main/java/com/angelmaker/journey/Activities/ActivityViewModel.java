package com.angelmaker.journey.Activities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journey.supportFiles.ActivityRepository;
import com.angelmaker.journeyDatabase.ActivityType;

import java.util.List;

/**
 * Created by AngelPlayer on 5/7/2018.
 */

public class ActivityViewModel extends AndroidViewModel{
    private ActivityRepository repository;
    private LiveData<List<String>> activityNames;
    private LiveData<List<ActivityInstance>> dailyActivities;
    private LiveData<List<String>> uniqueStarred;


    //Constructor for updateActivities recycler view
    public ActivityViewModel (Application application) {
        super(application);
        repository = new ActivityRepository(application);
        activityNames = repository.getActivityNames();
        uniqueStarred = repository.getUniqueStarred();
    }

    //Constructor for dailyActivities recycler view
    public ActivityViewModel (Application application, String viewedDate) {
        super(application);
        repository = new ActivityRepository(application, viewedDate);
        dailyActivities = repository.getDaysActivities();
    }



    //////////////////////////////////////////////////////////////////////////
    ///////////////////////Commands for activity types////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public List<String> getFinishedActivityTypes(String currentDate) { return repository.getFinishedActivityTypes(currentDate);}
    ActivityType getFullActivityTypes(String activityName) {return repository.getFullActivityType(activityName);}
    LiveData<List<String>> getActivityNames() {return activityNames;}
    public String getActivityDescription(String activityName) { return repository.getActivityDescription(activityName); }
    public void insertActivityType(ActivityType activityType) {repository.insertActivityType(activityType);}
    public void updateActivityType(ActivityType oldActivityType, ActivityType newActivityType) {repository.updateActivityType(oldActivityType, newActivityType); }
    public void deleteActivityType(String removeActivityName) {repository.deleteActivityType(removeActivityName); }
    public void deleteActivityType(ActivityType activityType) {repository.deleteActivityType(activityType); }



    //////////////////////////////////////////////////////////////////////////
    ///////////////////////Commands for activity instances////////////////////
    //////////////////////////////////////////////////////////////////////////

    public List<ActivityInstance> getNamedActivityInstances(String activityName) { return repository.getNamedActivityInstances(activityName);}
    LiveData<List<String>> getUniqueStarred() {return uniqueStarred;}
    LiveData<List<ActivityInstance>> getDailyActivities() {return dailyActivities;}
    public List<ActivityInstance> getSpecifiedDailyActivities(String specifiedDate) { return repository.getSpecifiedDailyActivities(specifiedDate); }
    public void insertActivityInstances(List<ActivityInstance> activityInstances) {repository.insertActivityInstances(activityInstances);}
    public void updateActivityInstance (ActivityInstance activityInstance) {repository.updateActivityInstance(activityInstance);}


    public void deleteActivityInstances(List<ActivityInstance> activityInstances, Context context) {
        //Get file URI for each entry and delete the file if it exists
        for(int i = 0; i < activityInstances.size() ; i++){
            ActivityInstance activity = activityInstances.get(i);
            if(activity.getAssociatedFile() != null) {
                Uri uri = Uri.parse(activity.getAssociatedFile());
                context.getContentResolver().delete(uri, null, null);
            }
        }

        repository.deleteActivityInstances(activityInstances);
    }


}

