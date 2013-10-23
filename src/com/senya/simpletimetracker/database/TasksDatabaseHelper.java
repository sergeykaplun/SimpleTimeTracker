package com.senya.simpletimetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.senya.simpletimetracker.models.Task;
import com.senya.simpletimetracker.models.WritableTask;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

/**
 * Created by sergeykaplun on 10/13/13.
 */
public class TasksDatabaseHelper{
    private static TasksDatabaseHelper _instance;
    private TasksDatabaseHelper(){}
    public static TasksDatabaseHelper getInstance(){
        if(_instance == null)
            _instance = new TasksDatabaseHelper();
        return _instance;
    }

    public long insertTask(Context c, ContentValues cv){
        return new DatabaseHelper(c).getWritableDatabase().insert(DatabaseHelper.TASKS_TABLE_NAME, null, cv);
    }

    public Cursor getDates(Context c){
        Cursor cursor = new DatabaseHelper(c).
                getWritableDatabase().
                //query(true, TaskContentProvider.TASKS_TABLE_NAME, new String[]{Task.COL_STR_CREATION_DATE}, null, null, null, null, null,null);
                        //query(false, TaskContentProvider.TASKS_TABLE_NAME, new String[]{"DISTINCT str_creation_date"}, null, null, null, null, null,null);
                rawQuery("SELECT _id , str_creation_date, creation_date FROM tasks GROUP BY str_creation_date ORDER BY creation_date DESC", null);
        return cursor;
    }

    public Cursor getRecordsByDate(Context c, String date){
        try{
            String[] args = {date};
            return new DatabaseHelper(c).getWritableDatabase().query(true, DatabaseHelper.TASKS_TABLE_NAME, null, "str_creation_date = ?", args, null, null, "creation_date DESC", null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public String[] getPreviousTitles(Context context){
        Cursor c = new DatabaseHelper(context).getWritableDatabase().
                rawQuery("SELECT DISTINCT title FROM tasks", null);
        String res[] = new String[c.getCount()];
        if(c.moveToFirst()){
            int pos = 0;
            do{
                res[pos++] = c.getString(0);
            }while(c.moveToNext());
        }
        return res;
    }

    public WritableTask getLastTask(Context c){
        WritableTask res;
        Cursor cursor = new DatabaseHelper(c).getWritableDatabase().rawQuery("select * from tasks order by start_date DESC limit 1", null);
        cursor.moveToFirst();
        res = new WritableTask(cursor);
        return res;
    }

    public boolean deleteTask(int taskId, Context context){
        return new DatabaseHelper(context).getWritableDatabase().delete(DatabaseHelper.TASKS_TABLE_NAME, "_id = ?", new String[]{String.valueOf(taskId)}) == 1;
    }

    public String getStringDate(Cursor cursor){
        return cursor.getString(cursor.getColumnIndex("str_creation_date"));
    }

    private static final long DAY = 1000*60*60*24;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
}
