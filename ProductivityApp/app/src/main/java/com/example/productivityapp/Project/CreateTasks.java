package com.example.productivityapp.Project;

public class CreateTasks {
    private String task;
    private String state = "pending";
    private String dueDate;

    public CreateTasks(String task, String state, String dueDate) {
        this.task = task;
        this.state = state;
        this.dueDate = dueDate;
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
}
