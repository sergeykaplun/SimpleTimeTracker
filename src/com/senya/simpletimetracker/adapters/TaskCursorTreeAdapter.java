package com.senya.simpletimetracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.senya.simpletimetracker.R;
import com.senya.simpletimetracker.database.TasksDatabaseHelper;
import com.senya.simpletimetracker.models.WritableTask;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sergeykaplun on 10/13/13.
 */
public class TaskCursorTreeAdapter extends CursorTreeAdapter {
    private Context context;
    Calendar calendar;
    public TaskCursorTreeAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        this.context = context;
        calendar = Calendar.getInstance();
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        String date = TasksDatabaseHelper.getInstance().getStringDate(cursor);
        return TasksDatabaseHelper.getInstance().getRecordsByDate(context, date);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        return View.inflate(context, R.layout.date_row, null);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
        String month = "";
        String day = "";
        try{
            Date creation_date = new Date();
            creation_date.setTime(cursor.getLong(cursor.getColumnIndex("creation_date")));
            month = cursor.getString(cursor.getColumnIndex("str_creation_date"));
            day = dayFormat.format(creation_date);
            month = df.format(TasksDatabaseHelper.sdf.parse(month));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        ((TextView)view.findViewById(R.id.day_textview)).setText(day);
        ((TextView)view.findViewById(R.id.month_textview)).setText(month);
    }

    static final Locale RU_LOCALE = new Locale("ru");
    static final DateFormatSymbols RU_SYMBOLS = new DateFormatSymbols(RU_LOCALE);
    static final String[] RU_MONTHS = {"Январь", "Февраль", "Март", "Апрел", "Май",
            "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    static {
        RU_SYMBOLS.setMonths(RU_MONTHS);
    }

    final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    final SimpleDateFormat df = new SimpleDateFormat("MMMM", RU_SYMBOLS);

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.task_row, null);
        ViewHolder vh = new ViewHolder();
        vh.title = (TextView)view.findViewById(R.id.task_title);
        vh.description = (TextView) view.findViewById(R.id.task_description);
        vh.timer = (TextView) view.findViewById(R.id.task_time_spend);
        vh.start = (ImageView) view.findViewById(R.id.task_button_start);
        vh.pause = (ImageView) view.findViewById(R.id.task_button_pause);
        vh.content_pane = view.findViewById(R.id.task_details_container);
        vh.circleBottomPart = (TextView) view.findViewById(R.id.date_bottom_part);
        vh.creation_date = (TextView) view.findViewById(R.id.creation_date);
        view.setTag(R.string.view_holder_tag, vh);
        return view;
    }

    @Override
    protected void bindChildView(View view, final Context context, Cursor cursor, boolean b) {
        final WritableTask t = new WritableTask(cursor);
        view.setTag(R.string.task_tag, t);
        ViewHolder vh = (ViewHolder) view.getTag(R.string.view_holder_tag);
        vh.title.setText(t.getTitle());
        if(TextUtils.isEmpty(t.getDescription())){
            vh.description.setVisibility(View.GONE);
        }else{
            vh.description.setText(t.getDescription());
            vh.description.setVisibility(View.VISIBLE);
        }
        vh.creation_date.setText(TaskListCursorAdapter.sdf.format(t.getCreationDate()));
        String viewString = t.getViewString(context, ":");
        vh.timer.setText(viewString);
        switch (t.getState()){
            case RUNNING:
                vh.timer.setTextColor(Color.RED);
                vh.start.setVisibility(View.GONE);
                vh.pause.setVisibility(View.VISIBLE);
                vh.pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.pause(context);
                        TaskCursorTreeAdapter.this.notifyDataSetChanged();
                    }
                });
                break;
            case PAUSED:
                vh.timer.setTextColor(Color.BLACK);
                vh.start.setVisibility(View.VISIBLE);
                vh.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.resume(context);
                        TaskCursorTreeAdapter.this.notifyDataSetChanged();
                    }
                });
                vh.pause.setVisibility(View.GONE);
                break;
            case NEW:
                vh.timer.setTextColor(Color.GREEN);
                vh.start.setVisibility(View.VISIBLE);
                vh.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.resume(context);
                        TaskCursorTreeAdapter.this.notifyDataSetChanged();
                    }
                });
                vh.pause.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        int bg;
        if(cursor.getPosition() == 0){
            vh.circleBottomPart.setVisibility(View.VISIBLE);
            calendar.setTimeInMillis(t.getCreationDate());
            vh.circleBottomPart.setText(DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            if(cursor.getCount() > 1){
                bg = R.drawable.task_first_row_bg;
            }else{
                bg = R.drawable.task_single_row_bg;
            }
        }else if(cursor.getPosition() == cursor.getCount() - 1){
            vh.circleBottomPart.setVisibility(View.GONE);
            bg = R.drawable.task_last_row_bg;
        }else{
            vh.circleBottomPart.setVisibility(View.GONE);
            bg = R.drawable.task_middle_row_bg;
        }
        vh.content_pane.setBackgroundResource(bg);
    }
    private static final String[] DAYS_OF_WEEK= {"Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"};

    public static class ViewHolder{
        public TextView title;
        public TextView description;
        public TextView timer;
        public ImageView start;
        public ImageView pause;
        public TextView creation_date;
        public View content_pane;
        public TextView circleBottomPart;
    }
}
