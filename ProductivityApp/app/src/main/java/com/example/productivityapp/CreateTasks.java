package com.example.productivityapp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateTasks {

    private String project = "project name";
    private String task = "Create task";
    private String description = "Tasks are used to break down a project into actional pieces. \\n \\n &#8226; Set dues dates to make task active.";
    private String state = "Pending";
    private String dueDate = "01 Jan 2022";

    private boolean isInProgress = false;
    private boolean isDone = false;
    public CreateTasks () {
        this.task = "Create task";
        this.description = "Tasks are used to break down a project into actional pieces. \\n \\n &#8226; Set dues dates to make task active.";
        this.state = "Pending";
        this.dueDate = "01 Jan 2022";
    }

    public CreateTasks(String task, String project) {
        this.task = task;
        this.project = project;
        this.state = "Pending";
        this.dueDate = getCurrentDate();
    }

    public CreateTasks(String mProject, String mTask, String desc, String dateDue, String mState){
        this.project = mProject;
        this.task = mTask;
        this.description = desc;
        this.dueDate = dateDue;
        this.state = mState;

        if(state.equalsIgnoreCase("In Progress")){
            this.isInProgress = true;
        }
        if(state.equalsIgnoreCase("Complete")){
            this.isDone = true;
        }
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

    public String getCurrentDate () {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String date = formatter.format(calendar.getTime());
        return date;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}