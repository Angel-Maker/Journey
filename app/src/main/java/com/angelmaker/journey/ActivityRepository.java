package com.angelmaker.journey;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by AngelPlayer on 5/7/2018.
 */

public class ActivityRepository {

    private ActivityDao activityDao;
    private LiveData<List<ActivityInstance>> allActivities;

    ActivityRepository(Application application) {
        ActivityRoomDatabase db = ActivityRoomDatabase.getDatabase(application);
        activityDao = db.activityDao();
        allActivities = activityDao.getAllActivities();
    }


    LiveData<List<ActivityInstance>> getAllActivities() {
        return allActivities;
    }

    public void insert (ActivityInstance activityInstance) {
        new insertAsyncTask(activityDao).execute(activityInstance);
    }

    private static class insertAsyncTask extends AsyncTask<ActivityInstance, Void, Void> {

        private ActivityDao asyncTaskDao;

        insertAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ActivityInstance... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }


    public void delete (ActivityInstance activityInstance) {
        new deleteAsyncTask(activityDao).execute(activityInstance);
    }

    private static class deleteAsyncTask extends AsyncTask<ActivityInstance, Void, Void> {

        private ActivityDao asyncTaskDao;

        deleteAsyncTask(ActivityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ActivityInstance... params) {
            asyncTaskDao.delete(params[0]);
            return null;
        }
    }
}


