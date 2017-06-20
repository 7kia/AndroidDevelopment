package com.example.lab1;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskViewHolder {
    private CheckBox mCheckBox;
    private TextView mHeader;
    private TextView mDate;
    private TextView mTime;
    private ImageView mIsComplete;
    private ImageView mImageView;

    public TaskViewHolder() {
    }

    public TaskViewHolder(
          TextView header,
          TextView date,
          CheckBox checkBox,
          ImageView isComplete,
          ImageView imageView,
          TextView time
    ) {
        this.mHeader = header;
        this.mDate = date;
        this.mCheckBox = checkBox;
        this.mIsComplete = isComplete;
        this.mImageView = imageView;
        this.mTime = time;
    }

    public CheckBox getCheckBox() {
        return mCheckBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.mCheckBox = checkBox;
    }

    public TextView getHeader() {
        return mHeader;
    }

    public void setHeader(TextView textView) {
        this.mHeader = textView;
    }

    public TextView getDate() {
        return mDate;
    }

    public void setDate(TextView textView) {
        this.mDate = textView;
    }

    public ImageView getComplete() {
        return mIsComplete;
    }

    public void setComplete(ImageView isComplete) {
        this.mIsComplete = isComplete;
    }

    public ImageView getImportante() {
        return mImageView;
    }

    public void setImportante(ImageView importante) {
        this.mImageView = importante;
    }

    public void setTime(TextView tv) {
        this.mTime = tv;
    }

    public TextView getTime() {
        return this.mTime;
    }
}