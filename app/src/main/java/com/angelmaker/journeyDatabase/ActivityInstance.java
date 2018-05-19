package com.angelmaker.journeyDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by AngelPlayer on 5/3/2018.
 *
 * Currently only the string 'androidActivity' is being implemented
 */

@Entity(tableName = "activities_table")   // What table the entry exists in
public class ActivityInstance {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "activityName")
    private String mActivityName;           //Activity name

    @ColumnInfo(name = "activityDescription")
    private String mActivityDescription;    //Activity Description

    @ColumnInfo(name = "startDate")
    private String mStartDate;              //Date the androidActivity was started on

    @ColumnInfo(name = "currentDate")
    private String mCurrentDate;            //Date of record instance

    @ColumnInfo(name = "endDate")
    private String mEndDate;                //Date the androidActivity will be finished

    @ColumnInfo(name = "completed")
    private boolean mCompleted = false;     //If the androidActivity has been completed

    @ColumnInfo(name = "starred")
    private boolean mStar = false;          //Marker for an important day (androidActivity will always appear in end presentation)

    @ColumnInfo(name = "note")
    private String mNote;                   //Notes made about a specific day's activities

    @ColumnInfo(name = "associatedFile")
    private String mAssociatedFile;         //Associated file for the androidActivity


    public ActivityInstance() {}

    public void setId(int newId){this.id = newId;}
    public void setActivityName(String activityName){this.mActivityName = activityName;}
    public void setActivityDescription(String activityDescription){this.mActivityDescription = activityDescription;}
    public void setStartDate(String startDate){this.mStartDate = startDate;}
    public void setCurrentDate(String currentDate){this.mCurrentDate = currentDate;}
    public void setEndDate(String endDate){this.mEndDate = endDate;}
    public void setCompleted(boolean completed){this.mCompleted = completed;}
    public void setStar(boolean star){this.mStar = star;}
    public void setNote(String note){this.mNote = note;}
    public void setAssociatedFile(String associatedFile){this.mAssociatedFile = associatedFile;}

    public int getId(){return this.id;}
    public String getActivityName(){return this.mActivityName;}
    public String getActivityDescription(){return this.mActivityDescription;}
    public String getStartDate(){return this.mStartDate;}
    public String getCurrentDate(){return this.mCurrentDate;}
    public String getEndDate(){return this.mEndDate;}
    public boolean getCompleted(){return this.mCompleted;}
    public boolean getStar(){return this.mStar;}
    public String getNote(){return this.mNote;}
    public String getAssociatedFile(){return this.mAssociatedFile;}
}




