package com.example.productivityapp;

public class Task {
    private String name;
    private String status;
    private String dueDate;
    private boolean isInprogress;
    private boolean isDone;

    public static final String DONE= "com.example.productivityapp.DONE";
    public static final String PENDING= "com.example.productivityapp.PENDING";
    public static final String IN_PROGRESS= "com.example.productivityapp.IN_PROGRESS";

    public Task(String name, String dueDate, String status) {
        this.name = name;
        this.dueDate = dueDate;

        if(status.equals(DONE)) {
            this.status = "done";
            this.isInprogress = false;
            isDone = true;
        } else if (status.equals(PENDING)) {
            this.status = "pending";
            this.isInprogress = false;
            this.isDone = false;
        }else if (status.equals(IN_PROGRESS)){
            this.status = "in-progress";
            this.isDone = false;
            this.isInprogress = true;
        }
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
