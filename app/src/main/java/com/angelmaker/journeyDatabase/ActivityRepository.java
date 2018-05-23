package com.angelmaker.journeyDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

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
        activityNames = activityDao.getUniqueActivityNames();
        uniqueStarred = activityDao.getUniqueStarred();
    }

    //Constructor for dailyActivities recycler view
    public ActivityRepository(Application application, String viewedDate) {
        ActivityRoomDatabase db = ActivityRoomDatabase.getDatabase(application);
        activityDao = db.activityDao();
        //Find all activities for a specific day
        daysActivities = activityDao.getDailyActivities(viewedDate);
    }

    public LiveData<List<String>> getUniqueStarred() { return uniqueStarred; }
    public LiveData<List<String>> getActivityNames() { return activityNames; }
    public LiveData<List<ActivityInstance>> getDaysActivities() { return daysActivities; }

    //Returns all of a named activity (used to find corresponding unique ID for updating and Progression Reflection)
    public List<ActivityInstance> getFullActivity(String getActivityName) {
        List<ActivityInstance> fullActivity = activityDao.getFullActivity(getActivityName);
        return fullActivity;
    }


    public List<ActivityInstance> getSpecifiedDailyActivities(String specifiedDate) {
        List<ActivityInstance> daysActivities = activityDao.getSpecifiedDailyActivities(specifiedDate);
        return daysActivities;
    }

    //Returns if database is empty
    public boolean checkIfEmpty(){
        Boolean empty = true;
        List<String> addedActivities = activityDao.checkIfEmpty();
        if (addedActivities.size() != 0 ){empty = false;}
        return empty;
    }


    //Adds a list of activities
    public void insertMany (List<ActivityInstance> activityInstances) {
        new insertManyAsyncTask(activityDao).execute(activityInstances);
    }

    private static class insertManyAsyncTask extends AsyncTask<List<ActivityInstance>, Void, Void> {

        private ActivityDao asyncTaskDao;

        insertManyAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ActivityInstance>... lists) {
            asyncTaskDao.insertMany(lists[0]);
            return null;
        }
    }


    //Updates a single entry
    public void update (ActivityInstance activityInstance) {
        new updateAsyncTask(activityDao).execute(activityInstance);
    }

    private static class updateAsyncTask extends AsyncTask<ActivityInstance, Void, Void> {

        private ActivityDao asyncTaskDao;

        updateAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ActivityInstance... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }



    //Updates a list of activities with the same primary ID
    public void updateMany (List<ActivityInstance> activityInstances) {
        new updateManyAsyncTask(activityDao).execute(activityInstances);
    }

    private static class updateManyAsyncTask extends AsyncTask<List<ActivityInstance>, Void, Void> {

        private ActivityDao asyncTaskDao;

        updateManyAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ActivityInstance>... lists) {
            asyncTaskDao.updateMany(lists[0]);
            return null;
        }
    }



    //Removes a list of activities
    public void deleteMany (List<ActivityInstance> activityInstances) {
        new deleteManyAsyncTask(activityDao).execute(activityInstances);
    }

    private static class deleteManyAsyncTask extends AsyncTask<List<ActivityInstance>, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteManyAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<ActivityInstance>... lists) {
            asyncTaskDao.deleteMany(lists[0]);
            return null;
        }
    }


    //Remove all instances of one activity category
    public void deleteFullActivity (String removeActivityName) {
        new deleteFullActivityAsyncTask(activityDao).execute(removeActivityName);
    }

    private static class deleteFullActivityAsyncTask extends AsyncTask<String, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteFullActivityAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteFullActivity(params[0]);
            return null;
        }
    }
}


