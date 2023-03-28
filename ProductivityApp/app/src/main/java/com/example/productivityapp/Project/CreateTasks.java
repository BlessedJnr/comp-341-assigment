package com.example.productivityapp.Project;

import java.util.ArrayList;

public class CreateTasks {

    private String project = "project name";
    private String task = "Create task";
    private String description = "Tasks are used to break down a project into actional pieces. \\n \\n &#8226; Set dues dates to make task active.";
    private String state = "Pending";
    private String dueDate = "01 Jan 2022";

    public CreateTasks () {

    }

    public CreateTasks(String task, String project) {
        this.task = task;
        this.project = project;
    }

    public CreateTasks(String mProject, String mTask, String desc, String dateDue, String mState){
        this.project = mProject;
        this.task = mTask;
        this.description = desc;
        this.dueDate = dateDue;
        this.state = mState;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }



}
