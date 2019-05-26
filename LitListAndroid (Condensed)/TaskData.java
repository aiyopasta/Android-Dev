package com.litlist;

import java.io.Serializable;
import java.util.Date;

public class TaskData implements Serializable {
    String taskTitle;
    String courseName;
    Date dueDate;
    int workSize;
    final static String DATEPREFIX = "Due: ";
    final static String COURSEPREFIX = " ";

    public TaskData(String title, String course, Date date, int size) {
        taskTitle = title;
        courseName = course;
        dueDate = date;
        workSize = size;
    }

    public TaskData() {}
}