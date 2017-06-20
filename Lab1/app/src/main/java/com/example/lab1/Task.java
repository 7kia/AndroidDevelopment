package com.example.lab1;

import java.sql.Date;
import java.util.Comparator;

public class Task {
    private String mHeader = "" ;
    private boolean mChecked = false ;
    private boolean mIsComplete = false;
    private String mDate;
    private String mDescription;
    private String mTime;
    private boolean mImportance;

    public Task(
            String name,
            String date,
            String time,
            String description,
            boolean isImportance,
            boolean isComplete
    ) {
        this.mHeader = name;
        this.mImportance = isImportance;
        this.mDate = date;
        this.mTime = time;
        this.mDescription = description;
        this.mIsComplete = isComplete;
    }

    public String getName()
    {
        return mHeader;
    }

    public void setName(String name)
    {
        this.mHeader = name;
    }

    public boolean isChecked()
    {
        return mChecked;
    }

    public void setChecked(boolean checked) {this.mChecked = checked;}

    public String getDate()
    {
        return mDate;
    }

    public void setDate(String date)
    {
        this.mDate = date;
    }

    public String getTime()
    {
        return mTime;
    }

    public void setTime(String time)
    {
        this.mTime = time;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String desc)
    {
        this.mDescription = desc;
    }

    public String toString()
    {
        return mHeader ;
    }

    public void toggleChecked()
    {
        mChecked = !mChecked ;
    }

    public void setComplete(boolean complete)
    {
        this.mIsComplete = complete;
    }

    public boolean getComplete()
    {
        return this.mIsComplete;
    }

    public void toggleComplete() {this.mIsComplete = !this.mIsComplete;}

    public void setImportance(boolean complete)
    {
        this.mImportance = complete;
    }

    public boolean getImportance()
    {
        return this.mImportance;
    }

}



