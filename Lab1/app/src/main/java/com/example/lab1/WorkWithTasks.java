package com.example.lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WorkWithTasks {
    private ArrayList<Task> mTasks;

    public WorkWithTasks(ArrayList<Task> tasks)
    {
        mTasks = tasks;
    }

    public void SelectAllTasks()
    {
        ChangeSelectionTasks(true);
    }

    public void UnselectAllTasks()
    {
        ChangeSelectionTasks(false);
    }

    private void ChangeSelectionTasks(Boolean value)
    {
        for(int i = 0; i < mTasks.size(); ++i)
        {
            mTasks.get(i).setChecked(value);
        }
    }

    public void DeleteAllTasks()
    {
        for(int i = 0; i < mTasks.size(); ++i)
        {
            if (mTasks.get(i).isChecked()) {
                mTasks.remove(i);
            }
        }
    }

    public void MarkAsDone(int pos)
    {
        Task task = mTasks.get(pos);
        task.setComplete(true);
        mTasks.set(pos, task);
    }

    public void Sort()
    {
        ArrayList<Task> completeTasks = new ArrayList<Task>();
        ArrayList<Task> uncompleteTasks = new ArrayList<Task>();
        for(int i = 0; i < mTasks.size(); ++i)
        {
            if(mTasks.get(i).getComplete())
            {
                completeTasks.add(mTasks.get(i));
            }
            else
            {
                uncompleteTasks.add(mTasks.get(i));
            }
        }

        Comparator<Task> compData = new Comparator<Task>(){
            public int compare(Task lhs, Task rhs){
                return lhs.getDate().compareTo(rhs.getDate());
            }
        };


        Collections.sort(completeTasks, compData);
        Collections.sort(uncompleteTasks, compData);

        mTasks.clear();
        mTasks.addAll(uncompleteTasks);
        mTasks.addAll(completeTasks);
    }
}
