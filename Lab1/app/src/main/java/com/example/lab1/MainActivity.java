package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    private static final int CM_UPDATE_ID = 1;
    private static final int CM_DELETE_ID = 2;
    private static final int CM_COMPLETE_ID = 3;

    private ListView mListView;
    private ArrayList<Task> mTasks;
    private ArrayAdapter<Task> mListAdapter;
    private int mNumber;
    private FileManager mFileManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.mainListTasks);
        mTasks = new ArrayList<>();
        mFileManager = new FileManager(
                this,
                mTasks,
                this.getString(R.string.fileName),
                this.getString(R.string.delimiter)
        );

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(this.getString(R.string.header));
        boolean isRewrite = intent.getBooleanExtra(this.getString(R.string.rewrite), false);
        if ((fileName != null) && !isRewrite) {
            updateTasks();
        }
        mFileManager.readTasksFromFile();

        setListView();
        
        registerForContextMenu(mListView);
    }

    private void setListView()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View item,
                    int position,
                    long id
            ) {
                Task task = mListAdapter.getItem(position);
                if(task != null)
                {
                    task.toggleChecked();
                    TaskViewHolder viewHolder = (TaskViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(task.isChecked());
                }
                else
                {
                    throw new NullPointerException(
                            "Task with index = "
                            + Integer.toString(position)
                            + " not founded"
                    );
                }
            }
        });

        mListAdapter = new TaskArrayAdapter(this, mTasks);
        mListView.setAdapter(mListAdapter);
    }

    private void updateTasks()
    {
        Intent intent = getIntent();
        String dateStr = intent.getStringExtra(this.getString(R.string.date));
        String desc = intent.getStringExtra(this.getString(R.string.desc));
        String time = intent.getStringExtra(this.getString(R.string.time));
        Boolean isImportance = intent.getBooleanExtra(this.getString(R.string.importance), true);
        mNumber = intent.getIntExtra(this.getString(R.string.number), -1);

        String header = intent.getStringExtra(this.getString(R.string.header));
        boolean taskIsChanged = intent.getBooleanExtra(this.getString(R.string.taskIsChanged), false);
        if (taskIsChanged)
        {
            editRecord(
                    header,
                    dateStr,
                    time,
                    desc,
                    isImportance
            );
        }
        else
        {
            mFileManager.writeTasksFromFile(
                new Task(
                    header,
                    dateStr,
                    time,
                    desc,
                    isImportance,
                    false
                )
            );
        }
    }

    private void editRecord(
            String header,
            String date,
            String time,
            String desc,
            boolean isImportance
    )
    {
        mFileManager.readTasksFromFile();
        Task task = mTasks.get(mNumber);
        task.setName(header);
        task.setDate(date);
        task.setTime(time);
        task.setDescription(desc);
        task.setImportance(isImportance);
        mTasks.set(mNumber, task);
        mFileManager.rewriteTasksFromFile();
        mTasks.clear();
    }

    public void onClick(View v) {
        if(v.getId() == R.id.addButton)
        {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra(this.getString(R.string.taskIsChanged), false);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(
            ContextMenu menu,
            View v,
            ContextMenuInfo menuInfo
    )
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(Menu.NONE, CM_UPDATE_ID, Menu.NONE, this.getString(R.string.menu_update));
        menu.add(Menu.NONE, CM_DELETE_ID, Menu.NONE, this.getString(R.string.menu_delete));
        menu.add(Menu.NONE, CM_COMPLETE_ID, Menu.NONE, this.getString(R.string.menu_complete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case CM_DELETE_ID:
                {
                    mTasks.remove(acmi.position);
                    sortTasks();
                    mFileManager.rewriteTasksFromFile();
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case CM_UPDATE_ID:
                {
                    Task selectedTask = mTasks.get(acmi.position);
                    Intent intent = new Intent(this, AddTaskActivity.class);
                    intent.putExtra(this.getString(R.string.header), selectedTask.getHeader());
                    intent.putExtra(this.getString(R.string.desc), selectedTask.getDescription());
                    intent.putExtra(this.getString(R.string.date), selectedTask.getDate());
                    intent.putExtra(this.getString(R.string.time), selectedTask.getTime());
                    intent.putExtra(this.getString(R.string.importance), selectedTask.getImportance());
                    intent.putExtra(this.getString(R.string.taskIsChanged), true);
                    intent.putExtra(this.getString(R.string.number), acmi.position);

                    startActivity(intent);
                }
                break;
            case CM_COMPLETE_ID:
                {
                    markTaskAsDone(acmi.position);
                    sortTasks();
                    mListAdapter = new TaskArrayAdapter(this, mTasks);
                    mListView.setAdapter( mListAdapter );
                    mListAdapter.notifyDataSetChanged();
                }
                break;
        }
        mFileManager.rewriteTasksFromFile();
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.select_all_settings:
                selectAllTasks();
                break;
            case R.id.remove_all_settings:
                unselectedAllTasks();
                break;
            case R.id.delete_all_settings:
                deleteAllTasks();
                sortTasks();
                break;
            case R.id.as_complete:
                for(int i = 0; i < mTasks.size(); ++i)
                {
                    if (mTasks.get(i).isChecked())
                    {
                        markTaskAsDone(i);
                    }
                }
                sortTasks();
                break;
        }
        mListAdapter.notifyDataSetChanged();
        mFileManager.rewriteTasksFromFile();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        getIntent().putExtra(this.getString(R.string.rewrite), true);
        super.onDestroy();
    }

    private void selectAllTasks()
    {
        changeSelectionTasks(true);
    }

    private void unselectedAllTasks()
    {
        changeSelectionTasks(false);
    }

    private void changeSelectionTasks(Boolean value)
    {
        for(int i = 0; i < mTasks.size(); ++i)
        {
            mTasks.get(i).setChecked(value);
        }
    }

    private void deleteAllTasks()
    {
        int i = 0;
        while(i < mTasks.size()) {
            if (mTasks.get(i).isChecked()) {
                mTasks.remove(i);
            } else {
                ++i;
            }
        }
    }

    private void markTaskAsDone(int index)
    {
        Task task = mTasks.get(index);
        task.setComplete(true);
        mTasks.set(index, task);
    }

    private void sortTasks()
    {
        ArrayList<Task> completeTasks = new ArrayList<Task>();
        ArrayList<Task> uncompletedTasks = new ArrayList<Task>();
        ArrayList<Task> importanceTasks = new ArrayList<Task>();
        for(int i = 0; i < mTasks.size(); ++i)
        {
            Task currentTask = mTasks.get(i);
            if(currentTask.getImportance() && !currentTask.getComplete())
            {
                importanceTasks.add(currentTask);
            }
            else if(currentTask.getComplete())
            {
                completeTasks.add(currentTask);
            }
            else
            {
                uncompletedTasks.add(currentTask);
            }
        }

        Comparator<Task> compData = new Comparator<Task>(){
            public int compare(Task lhs, Task rhs){
                return lhs.getDate().compareTo(rhs.getDate());
            }
        };


        Collections.sort(importanceTasks, compData);
        Collections.sort(uncompletedTasks, compData);
        Collections.sort(completeTasks, compData);

        mTasks.clear();
        mTasks.addAll(importanceTasks);
        mTasks.addAll(uncompletedTasks);
        mTasks.addAll(completeTasks);
    }

}


