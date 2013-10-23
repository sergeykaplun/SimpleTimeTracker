package com.senya.simpletimetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorTreeAdapter;

import com.senya.simpletimetracker.database.TasksDatabaseHelper;
import com.senya.simpletimetracker.models.Task;
import com.senya.simpletimetracker.views.DayStatsFragment;
import com.senya.simpletimetracker.views.NewTaskDialogFragment;
import com.senya.simpletimetracker.views.TaskDetailsFragment;
import com.senya.simpletimetracker.views.TasksListFragment;

import java.util.Date;

//import android.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements TasksListFragment.ClickListener, NewTaskDialogFragment.CreationListener{
    private TasksListFragment tasksListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //generateFakeData(new Date().getTime());
        setContentView(R.layout.activity_main);
        tasksListFragment = new TasksListFragment();
        setUpFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.action_new_task){
            new NewTaskDialogFragment().show(getSupportFragmentManager(), "new_task");
            return true;
        }else{
            return super.onMenuItemSelected(featureId, item);
        }
    }

    private void setUpFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tasksListFragment).commit();
    }

    //private long day = 1000*60*60*24;
    private void generateFakeData(long date){
    //    for(int i = 0; i < 10; i++){
            date -= 1000 * 60 * 60 * 24;
            ContentValues contentValues = new ContentValues(2);
            contentValues.put(Task.COL_TITLE, "FAKE");
            contentValues.put(Task.COL_DESCRIPTION, "fake");
            contentValues.put(Task.COL_CREATION_DATE, date);
            contentValues.put(Task.COL_STR_CREATION_DATE, TasksDatabaseHelper.sdf.format(date));
            contentValues.put(Task.COL_STATE, Task.TaskState.NEW.toString());
            TasksDatabaseHelper.getInstance().insertTask(this, contentValues);
            //this.getContentResolver().insert(TaskContentProvider.CONTENT_URI, contentValues);
    //    }
    }
    
    @Override
    public void onTaskClick(int id) {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new TaskDetailsFragment()).commit();
    }

    @Override
    public void onDayClick(int id) {
        Cursor c = TasksDatabaseHelper.getInstance().getDates(this);
        c.move(id + 1);
        String date = c.getString(c.getColumnIndex("str_creation_date"));
        c.close();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new DayStatsFragment(date)).commit();
    }

    private Handler handler = new Handler();
    @Override
    public void OnTaskCreated() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tasksListFragment.getAdapter().notifyDataSetChanged();
            }
        });
    }
}
