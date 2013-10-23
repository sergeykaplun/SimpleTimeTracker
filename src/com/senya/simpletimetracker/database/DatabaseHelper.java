package com.senya.simpletimetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergeykaplun on 10/21/13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "timeTrackerDb";
    public static final String TASKS_TABLE_NAME = "tasks";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TASKS_TABLE =
            " CREATE TABLE " + TASKS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " title TEXT NOT NULL, " +
                    " description TEXT NOT NULL, " +
                    " creation_date LONG NOT NULL, " +
                    " str_creation_date TEXT NOT NULL, " +
                    " start_date LONG, " +
                    " state TEXT NOT NULL);";

    static final String RUNTIME_PERIODS_TABLE_NAME = "runtimes";
    static final String CREATE_RUNTIME_PERIODS_TABLE =
            " CREATE TABLE " + RUNTIME_PERIODS_TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " task_id INTEGER NOT NULL, " +
                    " timestamp LONG NOT NULL, " +
                    " duration LONG);";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_RUNTIME_PERIODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TASKS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " +  RUNTIME_PERIODS_TABLE_NAME);
        onCreate(db);
    }
}
