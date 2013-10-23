package com.senya.simpletimetracker.adapters;

import java.text.SimpleDateFormat;

/**
 * Created by sergeykaplun on 10/10/13.
 */
public class TaskListCursorAdapter{//} extends CursorAdapter {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    /*private Context c;

    public TaskListCursorAdapter(Context context, Cursor c) {
        super(context, c, true);
        this.c = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.task_row, null);
        ViewHolder vh = new ViewHolder();
        vh.title = (TextView)view.findViewById(R.id.task_title);
        vh.description = (TextView) view.findViewById(R.id.task_description);
        vh.timer = (TimerTextView) view.findViewById(R.id.task_time_spend);
        vh.start = (ImageView) view.findViewById(R.id.task_button_start);
        vh.pause = (ImageView) view.findViewById(R.id.task_button_pause);
        //vh.creation_date = (TextView) view.findViewById(R.id.creation_date);
        view.setTag(vh);
        return view;
    }
    public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final WritableTask t = new WritableTask(cursor);
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.title.setText(t.getTitle());
        if(TextUtils.isEmpty(t.getDescription())){
            vh.description.setVisibility(View.GONE);
        }else{
            vh.description.setText(t.getDescription());
            vh.description.setVisibility(View.VISIBLE);
        }
        Object ttv = vh.timer.getTag();
        if(ttv != null){
            vh.timer.stopTimerThread();
        }
        vh.creation_date.setText(sdf.format(t.getCreationDate()));
        switch (t.getState()){
            case RUNNING:
                vh.timer.startTimerThread(t.getCreationDate(), t.getTotalPausesTime(c));
                vh.timer.setTextColor(Color.RED);
                vh.start.setVisibility(View.GONE);
                vh.pause.setVisibility(View.VISIBLE);
                vh.pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.pause(c);
                    }
                });
                break;
            case PAUSED:
                vh.timer.setText(TimerTextView.formatString(t.getLastPauseMilestone(c) - t.getStartDate() - t.getTotalPausesTime(c), ":"));
                vh.timer.setTextColor(Color.BLACK);
                vh.start.setVisibility(View.VISIBLE);
                vh.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.start(c);
                    }
                });
                vh.pause.setVisibility(View.GONE);
                break;
            case NEW:
                vh.timer.setText("New");
                vh.timer.setTextColor(Color.GREEN);
                vh.start.setVisibility(View.VISIBLE);
                vh.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        t.start(c);
                    }
                });
                vh.pause.setVisibility(View.GONE);
                break;
            default:

        }
    }

    private class ViewHolder{
        TextView title;
        TextView description;
        TimerTextView timer;
        ImageView start;
        ImageView pause;
        TextView creation_date;
    }*/
}
