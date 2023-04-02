package com.example.productivityapp;

public class Task {
    String name;
    String status;
    String dueDate;
    boolean isInprogress;
    boolean isDone;

    public Task(String name, String status, String dueDate, boolean isDone) {
        this.name = name;
        this.status = status;
        this.dueDate = dueDate;
        this.isInprogress = status.equalsIgnoreCase("in progress");
        this.isDone = isDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isInprogress() {
        return isInprogress;
    }

    public void setInprogress(boolean inprogress) {
        isInprogress = inprogress;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
