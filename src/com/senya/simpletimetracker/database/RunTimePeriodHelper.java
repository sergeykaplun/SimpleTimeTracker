package com.senya.simpletimetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.senya.simpletimetracker.models.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sergeykaplun on 10/12/13.
 */
public class RunTimePeriodHelper {
    private static RunTimePeriodHelper _instance;
    private SQLiteDatabase database;
    private RunTimePeriodHelper(Context c){
        database = new DatabaseHelper(c).getWritableDatabase();
    }

    public static RunTimePeriodHelper getInstance(Context context){
        if(_instance == null)
            _instance = new RunTimePeriodHelper(context);
        return _instance;
    }

    public void addRunTimePeriod(int taskId){
        database.beginTransaction();
        try{
            Cursor c = database.rawQuery("SELECT state FROM tasks WHERE _id = " + taskId, null);
            if(c.moveToFirst()){
                if(c.getString(0).equalsIgnoreCase(Task.TaskState.RUNNING.toString()))
                    throw new IllegalStateException("Trying to resume task with id - " + taskId + ". But it state already equals to RUNNING");
            }else{
                throw new IllegalStateException("Trying to resume task with id - " + taskId + ". But can't find it in tasks database");
            }

            ContentValues cv = new ContentValues(3);
            cv.put("task_id", taskId);
            cv.put("timestamp", new Date().getTime());
            cv.put("duration", 0);

            database.insert(DatabaseHelper.RUNTIME_PERIODS_TABLE_NAME, null, cv);
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
    }

    public void stopRunTimePeriod(int taskId){
        database.beginTransaction();
        try{
            Cursor c = database.rawQuery("SELECT state FROM tasks WHERE _id = " + taskId, null);
            if(c.moveToFirst()){
                if(!c.getString(0).equalsIgnoreCase(Task.TaskState.RUNNING.toString()))
                    throw new IllegalStateException("Trying to stop task with id - " + taskId + ". But it state not equals to RUNNING");
            }else{
                throw new IllegalStateException("Trying to resume task with id - " + taskId + ". But can't find it in tasks database");
            }
            c.close();
            c = database.rawQuery("SELECT _id, timestamp FROM runtimes WHERE task_id = " + taskId + " ORDER BY timestamp DESC LIMIT 1", null);
            int id;
            long lastTime;
            if(c.moveToFirst()){
                id = c.getInt(0);
                lastTime = c.getLong(1);
            }else{
                throw new IllegalStateException("Trying to stop task with id - " + taskId + ". But can't find last runtime in database.");
            }
            c.close();

            ContentValues cv = new ContentValues(1);
            cv.put("duration", new Date().getTime() - lastTime);

            database.update(DatabaseHelper.RUNTIME_PERIODS_TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
    }

    public List<Long> getRunTimePeriods(int taskId){
        List<Long> res = new ArrayList<Long>();
        database.beginTransaction();
        try{
            String query = "SELECT duration FROM " + DatabaseHelper.RUNTIME_PERIODS_TABLE_NAME + " WHERE task_id = " + taskId + " ORDER BY timestamp";
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()){
                do{
                    res.add(cursor.getLong(0));
                }while (cursor.moveToNext());
            }
            cursor.close();
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
        return res;
    }

    public long getLastRunTimeStart(int taskId){
        Cursor c = database.rawQuery("select timestamp from runtimes where task_id = " + taskId + " ORDER BY timestamp DESC LIMIT 1", null);
        c.moveToFirst();
        return c.getLong(0);
    }

    public boolean deleteRunTimePeriods(int taskId){
        return database.delete(DatabaseHelper.RUNTIME_PERIODS_TABLE_NAME, "task_id = ?", new String[]{String.valueOf(taskId)}) == 1;
    }
}
