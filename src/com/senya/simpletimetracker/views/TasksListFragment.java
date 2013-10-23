package com.senya.simpletimetracker.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;

import com.senya.simpletimetracker.R;
import com.senya.simpletimetracker.adapters.TaskCursorTreeAdapter;
import com.senya.simpletimetracker.database.RunTimePeriodHelper;
import com.senya.simpletimetracker.database.TasksDatabaseHelper;
import com.senya.simpletimetracker.models.Task;
import com.senya.simpletimetracker.models.WritableTask;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by sergeykaplun on 10/10/13.
 */
public class TasksListFragment extends ListFragment{
    public interface ClickListener{
        public void onTaskClick(int id);
        public void onDayClick(int id);
    }
    private CursorTreeAdapter adapter;
    private ExpandableListView listView;
    private Runnable updateRunnable;
    private ClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateRunnable = new UpdateRunnable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ExpandableListView(getActivity());
        listView.setGroupIndicator(null);
        listView.setId(android.R.id.list);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                listener.onTaskClick((int) l);
                return true;
            }
        });
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                listener.onDayClick(i);
                return true;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {
                new AlertDialog.Builder(getActivity()).setTitle("Delete this item").
                        setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int di) {
                                int id = ((Task) listView.getChildAt(i).getTag(R.string.task_tag)).getId();
                                if(TasksDatabaseHelper.getInstance().deleteTask(id, getActivity()))
                                    RunTimePeriodHelper.getInstance(getActivity()).deleteRunTimePeriods(id);
                                adapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        }).
                        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });

        listView.setDividerHeight(0);
        return listView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor cursor = TasksDatabaseHelper.getInstance().getDates(getActivity());//getActivity().getContentResolver().query(TaskContentProvider.CONTENT_URI, null, null, null, null);
        adapter = new TaskCursorTreeAdapter(cursor, getActivity());
        listView.setAdapter(adapter);
        for(int i = 0; i < adapter.getGroupCount(); i++)
            listView.expandGroup(i);
        runUpdater();
    }

    private void runUpdater(){
        handler = new Handler();
        handler.postDelayed(updateRunnable, 5000);
        //stpe.scheduleAtFixedRate(updateRunnable, 0, 500, TimeUnit.MILLISECONDS);
    }

    private ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
    private Handler handler;

    private boolean needColon = false;
    private class UpdateRunnable implements Runnable{
        @Override
        public void run() {
            int first = listView.getFirstVisiblePosition();
            int last = listView.getLastVisiblePosition();

            View tmpView;
            for(;first <= last; first++){
                tmpView = listView.getChildAt(first);
                if(tmpView != null)
                    if(tmpView.getTag(R.string.task_tag) != null){
                        WritableTask t = (WritableTask) tmpView.getTag(R.string.task_tag);
                        if(t.getState().equals(Task.TaskState.RUNNING)){
                            ((TaskCursorTreeAdapter.ViewHolder)tmpView.getTag(R.string.view_holder_tag)).timer.setText(t.getViewString(getActivity(), needColon?":":" "));
                        }
                    }
            }
            needColon = !needColon;
            if(handler != null)
                handler.postDelayed(this, 5000);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (ClickListener) activity;
    }

    @Override
    public void onPause() {
        handler = null;
        super.onPause();
    }

    public CursorTreeAdapter getAdapter() {
        return adapter;
    }
}
