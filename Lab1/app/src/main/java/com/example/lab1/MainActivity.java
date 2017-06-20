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

import com.example.lab1.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int CM_UPDATE_ID = 1;
    private static final int CM_DELETE_ID = 2;
    private static final int CM_COMPLETE_ID = 3;

    private ListView mListViewSimple;
    private ArrayList<Task> mTasks;
    private ArrayAdapter<Task> mListAdapter ;
    private int mNumber;
    private WorkWithTasks mWorkWithTasks;
    private FileManager mFileManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListViewSimple = (ListView) findViewById(R.id.mainListTasks);
        mTasks = new ArrayList<>();
        mWorkWithTasks = new WorkWithTasks(mTasks);
        mFileManager = new FileManager(
                this,
                mTasks,
                this.getString(R.string.fileName),
                this.getString(R.string.delimiter)
        );

        Intent intent = getIntent();
        String fileName = intent.getStringExtra(this.getString(R.string.header));
        Boolean isChange = intent.getBooleanExtra(this.getString(R.string.isChange), false);
        Boolean isRewrite = intent.getBooleanExtra(this.getString(R.string.rewrite), false);

        if ((fileName != null) && !isRewrite) {
            String dateStr = intent.getStringExtra(this.getString(R.string.date));
            String desc = intent.getStringExtra(this.getString(R.string.desc));
            String time = intent.getStringExtra(this.getString(R.string.time));
            Boolean isImportance = intent.getBooleanExtra(this.getString(R.string.importance), true);
            mNumber = intent.getIntExtra(this.getString(R.string.number), -1);

            if (isChange)
            {
                editRecord(
                        fileName,
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
                                fileName,
                                dateStr,
                                time,
                                desc,
                                isImportance,
                                false
                        )
                );
            }
        }
        mFileManager.readTasksFromFile();

        mListViewSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View item,
                    int position,
                    long id
            ) {
                Task task = mListAdapter.getItem(position);
                task.toggleChecked();
                TaskViewHolder viewHolder = (TaskViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(task.isChecked());
            }
        });

        mListAdapter = new TaskArrayAdapter(this, mTasks);
        mListViewSimple.setAdapter(mListAdapter);
        
        registerForContextMenu(mListViewSimple);
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
        switch (v.getId())
        {
            case R.id.addButton:
                Intent intent = new Intent(this, AddTaskActivity.class);
                intent.putExtra(this.getString(R.string.isChange), false);
                startActivity(intent);
                break;
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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
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
                    mWorkWithTasks.Sort();
                    mFileManager.rewriteTasksFromFile();
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case CM_UPDATE_ID:
                {
                    Task selectedTask = mTasks.get(acmi.position);
                    Intent intent = new Intent(this, AddTaskActivity.class);
                    intent.putExtra(this.getString(R.string.header), selectedTask.getName());
                    intent.putExtra(this.getString(R.string.desc), selectedTask.getDescription());
                    intent.putExtra(this.getString(R.string.date), selectedTask.getDate());
                    intent.putExtra(this.getString(R.string.time), selectedTask.getTime());
                    intent.putExtra(this.getString(R.string.importance), selectedTask.getImportance());
                    intent.putExtra(this.getString(R.string.isChange), true);
                    intent.putExtra(this.getString(R.string.number), acmi.position);
                    startActivity(intent);
                }
                break;
            case CM_COMPLETE_ID:
                {
                    mWorkWithTasks.MarkAsDone(acmi.position);
                    mWorkWithTasks.Sort();
                    mListAdapter = new TaskArrayAdapter(this, mTasks);
                    mListViewSimple.setAdapter( mListAdapter );
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
                mWorkWithTasks.SelectAllTasks();
                break;
            case R.id.remove_all_settings:
                mWorkWithTasks.UnselectAllTasks();
                break;
            case R.id.delete_all_settings:
                mWorkWithTasks.DeleteAllTasks();
                mWorkWithTasks.Sort();
                break;
            case R.id.as_complete:
                for(int i = 0; i < mTasks.size(); ++i)
                {
                    if (mTasks.get(i).isChecked())
                    {
                        mWorkWithTasks.MarkAsDone(i);
                    }
                }
                mWorkWithTasks.Sort();
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
}


