package com.angelmaker.journeyDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by AngelPlayer on 5/3/2018.
 */


@Dao
public interface ActivityDao {
    //////////////////////////////////////////////////////////////////////////
    ///////////////////////// Activity Type //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    //Adds a new type of activity
    @Insert
    void insertActivityType(ActivityType activityType);

    //Updates an activity type
    @Update
    void updateActivityType(ActivityType activityType);

    //Removes an activity using activity type
    @Delete
    void deleteActivityType(ActivityType activityType);

    //Removes an activity using activity name
    @Query ("DELETE from activity_types_table WHERE activityTypeName = :removeActivityName")
    void deleteActivityType(String removeActivityName);

    //Find all unique activity category names
    @Query ("SELECT activityTypeName FROM activity_types_table")
    LiveData<List<String>> getActivityTypeNames();

    //Find description from an activity name
    @Query ("SELECT activityDescription from activity_types_table WHERE activityTypeName = :activityName")
    String getActivityTypeDescription(String activityName);

    //Find description from an activity name
    @Query ("SELECT activityTypeName AND endDate FROM activity_types_table")
    List<String> getActivityTypeEndDate();





    //////////////////////////////////////////////////////////////////////////
    ///////////////////////// Activity Instance //////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    //Adds a list of activities
    @Insert
    void insertActivityInstances(List<ActivityInstance> activityInstanceList);

    //Updates a single entry
    @Update
    void updateActivityInstance(ActivityInstance activityInstance);

    //Removes a list of activities
    @Delete
    void deleteActivityInstances(List<ActivityInstance> activityInstance);

    //Finds all unique days that have a star on it
    @Query ("SELECT DISTINCT currentDate FROM activity_instance_table WHERE starred = 1")
    LiveData<List<String>> getUniqueStarred();

    //Find all activities for a day
    @Query ("SELECT * from activity_instance_table WHERE currentDate = :getActivityDate")
    LiveData<List<ActivityInstance>> getDailyActivities(String getActivityDate);

    //Find all activities for a specific day
    @Query ("SELECT * from activity_instance_table WHERE currentDate = :getActivityDate")
    List<ActivityInstance> getSpecifiedDailyActivities(String getActivityDate);
}

