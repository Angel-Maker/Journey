package com.angelmaker.journeyDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


/**
 * Created by AngelPlayer on 5/3/2018.
 */

@Database(entities = {ActivityInstance.class}, version = 4, exportSchema = false)
public abstract class ActivityRoomDatabase extends RoomDatabase {

    public abstract ActivityDao activityDao();

    private static ActivityRoomDatabase INSTANCE;

    static ActivityRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (ActivityRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ActivityRoomDatabase.class, "activities_db")
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
