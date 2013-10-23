package com.senya.simpletimetracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.senya.simpletimetracker.R;
import com.senya.simpletimetracker.database.TasksDatabaseHelper;
import com.senya.simpletimetracker.models.Task;
import com.senya.simpletimetracker.models.WritableTask;

/**
 * Created by sergeykaplun on 10/23/13.
 */
public class WidgetProvider extends AppWidgetProvider{
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_layout);
        WritableTask t = TasksDatabaseHelper.getInstance().getLastTask(context);

        Intent active = new Intent(context, WidgetProvider.class);
        active.setAction(ACTION_WIDGET_RECEIVER);
        active.putExtra("id", t.getId());

        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);

        remoteViews.setTextViewText(R.id.widget_project_title, t.getTitle());
        remoteViews.setTextViewText(R.id.widget_projects_quantity, "last");
        remoteViews.setTextViewText(R.id.widget_timer, t.getViewString(context, ":"));
        //remoteViews.setOnClickPendingIntent(R.id.widget_button, actionPendingIntent);

        //обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Ловим наш Broadcast, проверяем и выводим сообщение
        final String action = intent.getAction();
        if (ACTION_WIDGET_RECEIVER.equals(action)) {
            String msg = "null";
            try {
                msg = intent.getStringExtra("msg");
            } catch (NullPointerException e) {
                Log.e("Error", "msg = null");
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }
}
