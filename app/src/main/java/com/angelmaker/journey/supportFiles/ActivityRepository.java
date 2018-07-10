package com.angelmaker.journey.supportFiles;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.angelmaker.journeyDatabase.ActivityDao;
import com.angelmaker.journeyDatabase.ActivityInstance;
import com.angelmaker.journeyDatabase.ActivityRoomDatabase;
import com.angelmaker.journeyDatabase.ActivityType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by AngelPlayer on 5/7/2018.
 */

public class ActivityRepository {

    private ActivityDao activityDao;
    private LiveData<List<String>> activityNames;
    private LiveData<List<ActivityInstance>> daysActivities;
    private LiveData<List<String>> uniqueStarred;

    //Constructor for updateActivities recycler view
    public ActivityRepository(Application application) {
        ActivityRoomDatabase db = ActivityRoomDatabase.getDatabase(application);
        activityDao = db.activityDao();
        //Find all unique activity category names
        activityNames = activityDao.getActivityTypeNames();
        uniqueStarred = activityDao.getUniqueStarred();
    }

    //Constructor for dailyActivities recycler view
    public ActivityRepository(Application application, String viewedDate) {
        ActivityRoomDatabase db = ActivityRoomDatabase.getDatabase(application);
        activityDao = db.activityDao();
        //Find all activities for a specific day
        daysActivities = activityDao.getDailyActivities(viewedDate);
    }


    //////////////////////////////////////////////////////////////////////////
    ///////////////////////// Activity Type //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    public ArrayList<ActivityType> getFinishedActivityTypes(String currentDate) { return (ArrayList<ActivityType>) activityDao.getFinishedActivityTypes(currentDate);}
    public ActivityType getFullActivityType(String activityName) { return activityDao.getFullActivityType(activityName); }
    public List<ActivityType> getFullActivityTypesByDate(String date) { return activityDao.getFullActivityTypesByDate(date); }
    public LiveData<List<String>> getActivityNames() { return activityNames; }
    public String getActivityDescription(String activityName) { return activityDao.getActivityTypeDescription(activityName); }

    //Add a new activity type
    public void insertActivityType (ActivityType activityType) {
        new insertActivityTypeAsyncTask(activityDao).execute(activityType);
    }
    private static class insertActivityTypeAsyncTask extends AsyncTask<ActivityType, Void, Void> {

        private ActivityDao asyncTaskDao;

        insertActivityTypeAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ActivityType... activityTypes) {
            asyncTaskDao.insertActivityType(activityTypes[0]);
            return null;
        }
    }

    //Update an activity type
    public void updateActivityType (ActivityType oldActivityType, ActivityType newActivityType) {
        new updateActivityTypeAsyncTask(activityDao).execute(oldActivityType, newActivityType);
    }

    private static class updateActivityTypeAsyncTask extends AsyncTask<ActivityType, Void, Void> {

        private ActivityDao asyncTaskDao;

        updateActivityTypeAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ActivityType... activityTypes) {
            asyncTaskDao.updateActivityType(
                    activityTypes[0].getActivityTypeName(),             //Index to update
                    activityTypes[1].getActivityTypeName(),
                    activityTypes[1].getActivityDescription(),
                    activityTypes[1].getStartDate(),
                    activityTypes[1].getEndDate());
            return null;
        }
    }

    //Delete an activity type by reference
    public void deleteActivityType (ActivityType activityType) {
        new deleteActivityTypeAsyncTask(activityDao).execute(activityType);
    }
    private static class deleteActivityTypeAsyncTask extends AsyncTask<ActivityType, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteActivityTypeAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ActivityType... activityTypes) {
            asyncTaskDao.deleteActivityType(activityTypes[0]);
            return null;
        }
    }

    //Delete an activity type by name
    public void deleteActivityType (String removeActivityName) {
        new deleteActivityTypeByStringAsyncTask(activityDao).execute(removeActivityName);
    }
    private static class deleteActivityTypeByStringAsyncTask extends AsyncTask<String, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteActivityTypeByStringAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteActivityType(params[0]);
            return null;
        }
    }





    //////////////////////////////////////////////////////////////////////////
    ///////////////////////// Activity Instance //////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    public List<ActivityInstance> getNamedActivityInstances(String activityName) { return activityDao.getNamedActivityInstances(activityName);}
    public LiveData<List<String>> getUniqueStarred() { return uniqueStarred; }
    public LiveData<List<ActivityInstance>> getDaysActivities() { return daysActivities; }
    public List<ActivityInstance> getSpecifiedDailyActivities(String specifiedDate) {
        List<ActivityInstance> daysActivities = activityDao.getSpecifiedDailyActivities(specifiedDate);
        return daysActivities;
    }



    //Adds a list of activities
    public void insertActivityInstances (List<ActivityInstance> activityInstances) {
        new insertActivityInstancesAsyncTask(activityDao).execute(activityInstances);
    }
    private static class insertActivityInstancesAsyncTask extends AsyncTask<List<ActivityInstance>, Void, Void> {

        private ActivityDao asyncTaskDao;

        insertActivityInstancesAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ActivityInstance>... lists) {
            asyncTaskDao.insertActivityInstances(lists[0]);
            return null;
        }
    }


    //Updates a single entry
    public void updateActivityInstance (ActivityInstance activityInstance) {
        new updateActivityInstanceAsyncTask(activityDao).execute(activityInstance);
    }
    private static class updateActivityInstanceAsyncTask extends AsyncTask<ActivityInstance, Void, Void> {

        private ActivityDao asyncTaskDao;

        updateActivityInstanceAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ActivityInstance... params) {
            asyncTaskDao.updateActivityInstance(params[0]);
            return null;
        }
    }


    //Removes a list of activities
    public void deleteActivityInstances (List<ActivityInstance> activityInstances) {
        new deleteActivityInstancesAsyncTask(activityDao).execute(activityInstances);
    }
    private static class deleteActivityInstancesAsyncTask extends AsyncTask<List<ActivityInstance>, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteActivityInstancesAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ActivityInstance>... lists) {
            asyncTaskDao.deleteActivityInstances(lists[0]);
            return null;
        }
    }

}


