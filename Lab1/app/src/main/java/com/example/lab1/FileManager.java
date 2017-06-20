package com.example.lab1;

import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileManager {
    private static final int IS_IMPORTANCE_NUMBER = 4;
    private static final int IS_COMPLETE_NUMBER = 5;

    private ArrayList<Task> mTasks;
    private String mFileName;
    private String mDelimiter;
    private Context mContext;

    public FileManager(
            Context context,
            ArrayList<Task> tasks,
            String fileName,
            String del
    )
    {
        this.mContext = context;
        this.mTasks = tasks;
        this.mFileName = fileName;
        this.mDelimiter = del;
    }

    public void readTasksFromFile(){
        try{
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            mContext.openFileInput(mFileName)
                    )
            );
            String str = "";
            ArrayList<Task> importanceTasks = new ArrayList<>();
            ArrayList<Task> otherTasks = new ArrayList<>();
            ArrayList<Task> completedTasks = new ArrayList<>();
            while ((str = br.readLine()) != null)
            {
                String[] values = str.split(mDelimiter);
                int len = values.length;
                if (len > 1) {
                    String importance = (len > IS_IMPORTANCE_NUMBER )
                            ? String.valueOf(values[IS_IMPORTANCE_NUMBER])
                            : String.valueOf("false");

                    String complete = (len > IS_COMPLETE_NUMBER)
                            ? String.valueOf(values[IS_COMPLETE_NUMBER])
                            : String.valueOf("false");

                    boolean isImportance = (importance.equals(String.valueOf("true")));
                    boolean isComplete = (complete.equals(String.valueOf("true")));
                    Task newTask = new Task(
                            values[0],
                            values[1],
                            values[2],
                            values[3],
                            isImportance,
                            isComplete
                    );
                    if (isImportance && !isComplete) {
                        importanceTasks.add(newTask);
                    }
                    else if(isComplete){
                        completedTasks.add(newTask);
                    } else{
                        otherTasks.add(newTask);
                    }
                }
            }
            SortList(importanceTasks);
            SortList(otherTasks);
            mTasks.addAll(importanceTasks);
            mTasks.addAll(otherTasks);
            mTasks.addAll(completedTasks);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void writeTasksFromFile(Task task){
        try{
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                        mContext.openFileOutput(mFileName, mContext.MODE_APPEND)
                    )
            );
            bw.newLine();
            String importance = String.valueOf(task.getImportance());
            String complete = String.valueOf(task.getComplete());
            bw.write(
                    task.getName() + mDelimiter
                    + task.getDate() + mDelimiter
                    + task.getTime() + mDelimiter
                    + task.getDescription() + mDelimiter
                    + importance + mDelimiter
                    + complete
            );
            bw.close();
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void rewriteTasksFromFile(){
        try{
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            mContext.openFileOutput(mFileName, mContext.MODE_PRIVATE)
                    )
            );
            for(int i = 0; i != mTasks.size(); ++i)
            {
                Task task = mTasks.get(i);
                bw.write(
                        task.getName() + mDelimiter
                        + task.getDate() + mDelimiter
                        + task.getTime() + mDelimiter
                        + task.getDescription() + mDelimiter
                        + String.valueOf(task.getImportance()) + mDelimiter
                        + String.valueOf(task.getComplete())
                );
                if (i + 1 != mTasks.size()) {
                    bw.newLine();
                }
            }
            bw.close();
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private void SortList(ArrayList<Task> tasks)
    {
        Collections.sort(tasks, new Comparator<Task>(){
            public int compare(Task lhs, Task rhs){
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
    }
}
