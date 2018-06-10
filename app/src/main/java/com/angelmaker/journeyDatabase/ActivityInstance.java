package com.angelmaker.journeyDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by AngelPlayer on 5/3/2018.
 **/

@Entity(tableName = "activity_instance_table",
        foreignKeys = @ForeignKey(entity = ActivityType.class,
                                    parentColumns = "activityTypeName",
                                    childColumns = "activityInstanceName",
                                    onDelete = 5,
                                    onUpdate = 5))  //5 = Cascade

public class ActivityInstance {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "activityInstanceName")
    private String mActivityInstanceName;           //Activity name

    @ColumnInfo(name = "currentDate")
    private String mCurrentDate;            //Date of record instance

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
    public void setActivityInstanceName(String activityInstanceName){this.mActivityInstanceName = activityInstanceName;}
    public void setCurrentDate(String currentDate){this.mCurrentDate = currentDate;}
    public void setCompleted(boolean completed){this.mCompleted = completed;}
    public void setStar(boolean star){this.mStar = star;}
    public void setNote(String note){this.mNote = note;}
    public void setAssociatedFile(String associatedFile){this.mAssociatedFile = associatedFile;}

    public int getId(){return this.id;}
    public String getActivityInstanceName(){return this.mActivityInstanceName;}
    public String getCurrentDate(){return this.mCurrentDate;}
    public boolean getCompleted(){return this.mCompleted;}
    public boolean getStar(){return this.mStar;}
    public String getNote(){return this.mNote;}
    public String getAssociatedFile(){return this.mAssociatedFile;}
}




