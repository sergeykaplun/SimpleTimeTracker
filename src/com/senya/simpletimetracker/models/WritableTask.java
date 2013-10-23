package com.senya.simpletimetracker.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.senya.simpletimetracker.database.DatabaseHelper;
import com.senya.simpletimetracker.database.RunTimePeriodHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by sergeykaplun on 10/12/13.
 */
public class WritableTask extends Task{
    public WritableTask(Cursor c) {
        super(c);
    }

    public void pause(final Context c){
        RunTimePeriodHelper.getInstance(c).stopRunTimePeriod(id);

        ContentValues cv = new ContentValues();
        cv.put(Task.COL_STATE, Task.TaskState.PAUSED.toString());
        int rows = new DatabaseHelper(c).getWritableDatabase().update(DatabaseHelper.TASKS_TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
        if(rows != 1){
            throw new IllegalStateException("Error while trying to pause task. No any row affected");
        }
    }

    public void resume(final Context c){
        switch(state){
            case RUNNING:
                throw new IllegalStateException("Error while trying to start task with id " + id + ". Task is running now.");
            case PAUSED:
                RunTimePeriodHelper.getInstance(c).addRunTimePeriod(id);
            case NEW:
                ContentValues cv = new ContentValues(1);
                cv.put("start_date", new Date().getTime());
                int rows = new DatabaseHelper(c).getWritableDatabase().update(DatabaseHelper.TASKS_TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
                if(rows != 1){
                    throw new IllegalStateException("Error while trying to start task. No any row affected");
                }
                RunTimePeriodHelper.getInstance(c).addRunTimePeriod(id);
                break;
        }
        ContentValues cv = new ContentValues();
        cv.put(Task.COL_STATE, Task.TaskState.RUNNING.toString());
        int rows = new DatabaseHelper(c).getWritableDatabase().update(DatabaseHelper.TASKS_TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
        if(rows != 1){
            throw new IllegalStateException("Error while trying to pause task. No any row affected");
        }
    }

    public long getTotalRunTime(Context c){
        if(this.state.equals(TaskState.NEW))
            throw new IllegalStateException("Error while trying to count pauses for project with id" + this.id + ". Task were never started.");

        List<Long> pauses = RunTimePeriodHelper.getInstance(c).getRunTimePeriods(this.id);

        if(pauses.size() == 0)
            return 0;

        long res = 0;
        for(int i = 0; i < pauses.size(); i++){
            res += pauses.get(i);
        }
        switch (state){
            case RUNNING:
                return res + (new Date().getTime() - RunTimePeriodHelper.getInstance(c).getLastRunTimeStart(id));
            case PAUSED:
                return res;
            default:
                throw new IllegalStateException("Can't calculate pauses for state " + state.toString());
        }
    }

    public String getViewString(Context context, String separator){
        if(context == null)
            return "";

        if(state.equals(TaskState.NEW)){
                return "New";
        }else{
            return formatString(getTotalRunTime(context), separator);
        }
    }

    private static String formatString(long millisUntilFinished, String separator){
        int hours = Math.abs((int) (millisUntilFinished/HOUR));
        int minutes = Math.abs((int) ((millisUntilFinished%HOUR)/MINUTE));
        return (hours<10?"0"+hours:hours) + separator + (minutes<10?"0" + minutes:minutes);
    }

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;
}
