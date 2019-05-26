package com.litlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class UserData implements Serializable{
    public ArrayList<TaskData> tasks;
    public ArrayList<String> courseNames;
    public long id;
    public String username = "";

    public UserData() {
        tasks = new ArrayList<>();
        courseNames = new ArrayList<>();

        String tempID = "";
        for (int i=0;i<10;i++) {
            tempID+=("" + new Random().nextInt(10));
        }
        id = Long.parseLong(tempID);
    }
}