package com.example.productivityapp;

import java.util.List;


public class Project {
    String name;
    boolean isComplete;
    List<Task> tasks;

    public Project(String name, boolean isComplete) {
        this.name = name;
        this.isComplete = isComplete;
    }

    public Project(String name, boolean isComplete, List<Task> tasks) {
        this.name = name;
        this.isComplete = isComplete;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean complete) {
        this.isComplete = complete;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
