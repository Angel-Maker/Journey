package com.angelmaker.journey;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by AngelPlayer on 5/3/2018.
 */


@Dao
public interface ActivityDao {

    @Insert    //There is also @Delete   and   @Update
    void insert(ActivityInstance activityInstance);

    @Delete
    void delete(ActivityInstance activityInstance);

    @Query("DELETE FROM activities_table")
    void deleteAll();

    @Query ("SELECT * from activities_table ORDER BY activity ASC")
    LiveData<List<ActivityInstance>> getAllActivities();

    @Query ("SELECT * from activities_table WHERE activity = :activity LIMIT 1")
    LiveData<ActivityInstance> getActivity(String activity);
}
