package com.angelmaker.journey;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by AngelPlayer on 5/3/2018.
 *
 * Currently only the string 'activity' is being implemented
 */

@Entity(tableName = "activities_table")   // What table the entry exists in
public class ActivityInstance {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "activity")    //What column mWord belongs to (defaults to "mWord" if not specified)
    private String mActivity;

    public ActivityInstance(String activity) {this.mActivity = activity;}

    public String getActivity(){return this.mActivity;}
    public int getId(){return this.id;}
    public void setId(int newId){this.id = newId;};
}





/*
@Entity(tableName = "activities_table")
public class ActivityInstance {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "word")
    private String mActivity;        //Name of the activity

    public ActivityInstance(String newActivity){this.mActivity = newActivity;}

    public String getActivity(){return this.mActivity;}








    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private int id;

    @ColumnInfo
    private int date = 1;           //Date the activity was done on    (Update later to date type variable)

    @ColumnInfo
    private boolean completed = false;     //If the activity has been completed

    @ColumnInfo
    private String associatedFile;  //Will be used later to link relevant documentation of completion



    public String getId(){return this.id;}
    public String getDate(){return this.date;}
    public String getCompleted(){return this.completed;}
    public String getAssociatedFile(){return this.associatedFile;}
*/