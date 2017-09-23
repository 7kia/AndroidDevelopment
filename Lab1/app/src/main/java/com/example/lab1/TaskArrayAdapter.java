package com.example.lab1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TaskArrayAdapter extends ArrayAdapter<Task> {

    private LayoutInflater mInflater;

    public TaskArrayAdapter(Context context, List<Task> planetList ) {
        super( context, R.layout.task, R.id.task, planetList );
        mInflater = LayoutInflater.from(context) ;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {
        Task task = (Task) this.getItem( position );

        CheckBox isComplete;
        ImageView newCheckBox;
        TextView header;
        TextView date;
        TextView time;
        ImageView view;

        if ( convertView == null ) {
            convertView = mInflater.inflate(R.layout.task, null);

            header = (TextView) convertView.findViewById( R.id.header );
            isComplete = (CheckBox) convertView.findViewById( R.id.myCheckBox );
            date = (TextView) convertView.findViewById(R.id.date);
            newCheckBox = (ImageView) convertView.findViewById(R.id.isComplete);
            view = (ImageView) convertView.findViewById(R.id.importance);
            time = (TextView) convertView.findViewById(R.id.textViewTime);
            convertView.setTag(
                    new TaskViewHolder(
                        header,
                        date,
                        isComplete,
                        newCheckBox,
                        view,
                        time
                    )
            );

            isComplete.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Task task = (Task) cb.getTag();
                    task.setChecked( cb.isChecked() );
                }
            });
        }
        else {
            TaskViewHolder viewHolder = (TaskViewHolder) convertView.getTag();
            isComplete = viewHolder.getCheckBox() ;
            header = viewHolder.getHeader() ;
            date = viewHolder.getDate();
            newCheckBox = viewHolder.getComplete();
            view = viewHolder.getImportante();
            time = viewHolder.getTime();
        }

        isComplete.setTag( task );
        newCheckBox.setTag(task);

        isComplete.setChecked( task.isChecked() );
        header.setText( task.getName() );
        date.setText(task.getDate());
        time.setText(task.getTime());
        if(task.getComplete())
        {
            newCheckBox.setImageResource(R.drawable.task_done_1);
        }
        else
        {
            newCheckBox.setImageResource(R.drawable.task_to_process);
        }
        if (task.getImportance())
        {
            view.setImageResource(R.drawable.alarm_clock);
        }

        return convertView;
    }

}
