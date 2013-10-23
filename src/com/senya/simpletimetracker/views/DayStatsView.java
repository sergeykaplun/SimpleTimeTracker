package com.senya.simpletimetracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sergeykaplun on 10/13/13.
 */
public class DayStatsView extends View {
    private String date;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DayStatsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDateToView(String date){
        this.date = date;
    }

    private void init(){}

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if(!TextUtils.isEmpty(date)){
            paint.setColor(Color.RED);
            canvas.drawText(date, getWidth()/2, getHeight()/2, paint);
        }
    }
}
