package com.senya.simpletimetracker.views;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.senya.simpletimetracker.R;
import com.senya.simpletimetracker.database.TasksDatabaseHelper;

/**
 * Created by sergeykaplun on 10/20/13.
 */
public class DayStatsFragment extends Fragment{
    private String strCreationDate;
    public DayStatsFragment(String day){
        strCreationDate = day;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.day_stats_layout, null);
        ((TextView)v.findViewById(R.id.date_text_view)).setText(strCreationDate);
        ((DayStatsView)v.findViewById(R.id.day_stats_view)).setDateToView(strCreationDate);
        return v;
    }
}
