package com.example.lab1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.app.TimePickerDialog;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends Activity implements View.OnClickListener {
    private Spinner mSpinner;
    private EditText mHeader;
    private EditText mDescription;
    private EditText mTime;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private SimpleDateFormat mDateFormatter;
    private CheckBox mImportance;
    private String mSpinnerValue;
    private Boolean mIsChange;
    private int mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mHeader = (EditText) findViewById(R.id.headerText);
        mDescription = (EditText) findViewById(R.id.description);
        mImportance = (CheckBox) findViewById(R.id.radioImportance);


        mDateFormatter = new SimpleDateFormat(this.getString(R.string.format), Locale.US);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mTime = (EditText) findViewById(R.id.time);
        setDateTime();

        Intent intent = getIntent();
        String header = intent.getStringExtra(this.getString(R.string.header));

        mNumber = intent.getIntExtra(this.getString(R.string.number), -1);
        mIsChange = intent.getBooleanExtra(this.getString(R.string.taskIsChanged), false);
        if (header != null)
        {
            String date = intent.getStringExtra(this.getString(R.string.date));
            String time = intent.getStringExtra(this.getString(R.string.time));
            String desc = intent.getStringExtra(this.getString(R.string.desc));
            boolean isImportance = intent.getBooleanExtra(this.getString(R.string.importance), false);

            mHeader.setText(header);
            mDescription.setText(desc);
            setNewValuesSpinner(date);
            mImportance.setChecked(isImportance);
            mTime.setText(time);
        }

        mSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDatePickerDialog.show();
                return false;
            }
        });

        mTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTimePickerDialog.show();
                return false;
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int pos,
                    long id
            ) {
                // TODO Auto-generated method stub
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                mSpinnerValue = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ready:
            {
                Intent newTask = new Intent(v.getContext(), MainActivity.class);
                newTask.putExtra(this.getString(R.string.header), mHeader.getText().toString());
                newTask.putExtra(this.getString(R.string.desc), mDescription.getText().toString());
                newTask.putExtra(this.getString(R.string.date), mSpinnerValue);
                newTask.putExtra(this.getString(R.string.time), mTime.getText().toString());
                newTask.putExtra(this.getString(R.string.number), mNumber);
                newTask.putExtra(this.getString(R.string.taskIsChanged), mIsChange);
                newTask.putExtra(this.getString(R.string.importance), mImportance.isChecked());

                startActivity(newTask);
                finish();
            }
            case R.id.cancel:
            {
                finish();
            }
            break;
        }
    }

    private void setDateTime()
    {
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(
            this,
            new OnDateSetListener() {
                public void onDateSet(
                        DatePicker view,
                        int year,
                        int monthOfYear,
                        int dayOfMonth
                ) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    setNewVal(mDateFormatter.format(newDate.getTime()));
                }
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        );

        mTimePickerDialog = new TimePickerDialog(
            this,
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(
                        TimePicker view,
                        int hourOfDay,
                        int minute
                ) {
                    String editTextTimeParam = hourOfDay + " : " + minute;
                    mTime.setText(editTextTimeParam);
                }
            },
            newCalendar.get(Calendar.HOUR_OF_DAY),
            newCalendar.get(Calendar.MINUTE),
            false
        );
    }

    private void setNewValuesSpinner(String date)
    {
        setNewVal(date);
    }

    private void setNewVal(String date)
    {
        String[] data = {date};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            getApplicationContext(),
            android.R.layout.simple_spinner_item,
            data
        );
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
    }
}
