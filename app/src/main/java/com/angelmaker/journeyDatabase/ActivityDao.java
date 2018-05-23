package com.angelmaker.journeyDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.Calendar;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Created by AngelPlayer on 5/3/2018.
 */


@Dao
public interface ActivityDao {
    //Find all unique activity category names
    @Query ("SELECT DISTINCT activityName FROM activities_table")
    LiveData<List<String>> getUniqueActivityNames();

    //Find all unique activity category names
    @Query ("SELECT DISTINCT activityName FROM activities_table")
    List<String> checkIfEmpty();

    //Finds all unique days that have a star on it
    @Query ("SELECT DISTINCT currentDate FROM activities_table WHERE starred = 1")
    LiveData<List<String>> getUniqueStarred();

    //Adds a list of activities
    @Insert
    void insertMany(List<ActivityInstance> activityInstanceList);

    //Remove all instances of one activity category
    @Query ("DELETE from activities_table WHERE activityName = :removeActivityName")
    void deleteFullActivity(String removeActivityName);

    //Removes a list of activities
    @Delete
    void deleteMany(List<ActivityInstance> activityInstance);

    //Updates a list of activities with the same primary ID
    @Update
    void updateMany(List<ActivityInstance> activityInstance);

    //Returns all of a named activity (used to find corresponding unique ID for updating and Progression Reflection)
    @Query ("Select * from activities_table WHERE activityName = :getActivityName")
    List<ActivityInstance> getFullActivity(String getActivityName);

    //Find all activities for a day
    @Query ("SELECT * from activities_table WHERE currentDate = :getActivityDate")
    LiveData<List<ActivityInstance>> getDailyActivities(String getActivityDate);

    //Find all activities for a specific day
    @Query ("SELECT * from activities_table WHERE currentDate = :getActivityDate")
    List<ActivityInstance> getSpecifiedDailyActivities(String getActivityDate);

    //Updates a single entry
    @Update
    void update(ActivityInstance activityInstance);
}

