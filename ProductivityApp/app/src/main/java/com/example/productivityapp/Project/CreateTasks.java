package com.example.productivityapp.Project;

import java.util.ArrayList;

public class CreateTasks {
    private String task;
    private String state = "pending";
    private String dueDate;
    private ArrayList<String> collaborators;

    public CreateTasks(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
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

    public ArrayList<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(ArrayList<String> collaborators) {
        this.collaborators = collaborators;
    }
}
