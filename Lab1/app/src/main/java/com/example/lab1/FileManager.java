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

class FileManager {
    private static final int IMPORTANCE_ID = 4;
    private static final int COMPLETE_ID = 5;
    private static final int AMOUNT_DATA = 6;
    private ArrayList<Task> mTasks;
    private String mFileName;
    private String mDelimiter;
    private Context mContext;
    private ArrayList<Task> mImportanceTasks;
    private ArrayList<Task> mOtherTasks;
    private ArrayList<Task> mCompletedTasks;

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

        recreateTaskLists();
    }

    private void recreateTaskLists()
    {
        mImportanceTasks = new ArrayList<>();
        mOtherTasks = new ArrayList<>();
        mCompletedTasks = new ArrayList<>();
    }

    public void readTasksFromFile(){
        try{
            recreateTaskLists();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            mContext.openFileInput(mFileName)
                    )
            );
            String readString;
            while ((readString = br.readLine()) != null)
            {
                parseTask(readString);
            }

            SortList(mImportanceTasks);
            SortList(mOtherTasks);
            SortList(mCompletedTasks);
            mTasks.addAll(mImportanceTasks);
            mTasks.addAll(mOtherTasks);
            mTasks.addAll(mCompletedTasks);
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.println("Task file not found");
        } catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Task file not read");
        }
    }

    private void parseTask(String readString) throws IOException
    {
        String[] values = readString.split(mDelimiter);
        if (values.length == AMOUNT_DATA) {
            String importance = values[IMPORTANCE_ID];
            String complete = values[COMPLETE_ID];

            boolean isImportance = importance.equals("true");
            boolean isComplete = complete.equals("true");
            Task newTask = new Task(
                    values[0],
                    values[1],
                    values[2],
                    values[3],
                    isImportance,
                    isComplete
            );
            if (isImportance && !isComplete) {
                mImportanceTasks.add(newTask);
            } else if(isComplete){
                mOtherTasks.add(newTask);
            } else{
                mCompletedTasks.add(newTask);
            }
        }
    }


    void writeTasksFromFile(Task task){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                        mContext.openFileOutput(mFileName, mContext.MODE_APPEND)
                    )
            );

            bufferedWriter.newLine();
            String importance = String.valueOf(task.getImportance());
            String complete = String.valueOf(task.getComplete());

            bufferedWriter.write(
                    task.getHeader() + mDelimiter
                    + task.getDate() + mDelimiter
                    + task.getTime() + mDelimiter
                    + task.getDescription() + mDelimiter
                    + importance + mDelimiter
                    + complete
            );
            bufferedWriter.close();
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.println("Task file not found");
        } catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Write error to task file");
        }
    }

    public void rewriteTasksFromFile(){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            mContext.openFileOutput(mFileName, mContext.MODE_PRIVATE)
                    )
            );
            for(int i = 0; i != mTasks.size(); ++i)
            {
                Task task = mTasks.get(i);
                bufferedWriter.write(
                        task.getHeader() + mDelimiter
                        + task.getDate() + mDelimiter
                        + task.getTime() + mDelimiter
                        + task.getDescription() + mDelimiter
                        + String.valueOf(task.getImportance()) + mDelimiter
                        + String.valueOf(task.getComplete())
                );
                if (i + 1 != mTasks.size()) {
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();
        } catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.println("Task file not found");
        } catch (IOException ex){
            ex.printStackTrace();
            System.out.println("Read error to task file");
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
