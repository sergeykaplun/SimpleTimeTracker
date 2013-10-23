package com.senya.simpletimetracker.models;

import android.database.Cursor;

/**
 * Created by sergeykaplun on 10/10/13.
 */
public class Task {
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CREATION_DATE = "creation_date";
    public static final String COL_STR_CREATION_DATE = "str_creation_date";
    public static final String COL_START_DATE = "start_date";
    public static final String COL_STATE = "state";

    public enum TaskState{
        NEW,
        RUNNING,
        PAUSED;
        //STOPPED;
    }

    protected int id;
    protected String title;
    protected String description;
    protected long creationDate;

    public String getStrCreationDate() {
        return strCreationDate;
    }

    private Task setStrCreationDate(String strCreationDate) {
        this.strCreationDate = strCreationDate;
        return this;
    }

    protected String strCreationDate;
    protected long startDate;
    protected TaskState state;

    public Task(Cursor c){
        this.setId(c.getInt(c.getColumnIndex(COL_ID))).
                setTitle(c.getString(c.getColumnIndex(COL_TITLE))).
                setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION))).
                setCreationDate(c.getLong(c.getColumnIndex(COL_CREATION_DATE))).
                        setStrCreationDate(c.getString(c.getColumnIndex(COL_STR_CREATION_DATE))).
                setStartDate(c.getLong(c.getColumnIndex(COL_START_DATE))).
                setState(c.getString(c.getColumnIndex(COL_STATE)));
    }

    public long getCreationDate() {
        return creationDate;
    }

    private Task setCreationDate(long creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    private Task setState(String state) {
        this.state = TaskState.valueOf(state);
        return this;
    }

    public TaskState getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    private Task setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    private Task setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    private Task setDescription(String description) {
        this.description = description;
        return this;
    }

    public long getStartDate() {
        return startDate;
    }

    private Task setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }
}
