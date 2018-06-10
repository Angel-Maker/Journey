package com.angelmaker.journeyDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "activity_types_table")   // What table the entry exists in
public class ActivityType {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "activityTypeName")
    private String mActivityTypeName;           //Activity name

    @ColumnInfo(name = "activityDescription")
    private String mActivityDescription;    //Activity Description

    @ColumnInfo(name = "startDate")
    private String mStartDate;              //Date the androidActivity was started on

    @ColumnInfo(name = "endDate")
    private String mEndDate;                //Date the androidActivity will be finished

    public void setActivityTypeName(String activityTypeName){this.mActivityTypeName = activityTypeName;}
    public void setActivityDescription(String activityDescription){this.mActivityDescription = activityDescription;}
    public void setStartDate(String startDate){this.mStartDate = startDate;}
    public void setEndDate(String endDate){this.mEndDate = endDate;}

    public String getActivityTypeName(){return this.mActivityTypeName;}
    public String getActivityDescription(){return this.mActivityDescription;}
    public String getStartDate(){return this.mStartDate;}
    public String getEndDate(){return this.mEndDate;}
}
