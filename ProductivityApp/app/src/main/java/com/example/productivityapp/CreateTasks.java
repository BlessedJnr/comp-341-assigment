package com.example.productivityapp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class CreateTasks {

    private String project = "project name";
    private String task = "Create task";
    private String description = "Tasks are used to break down a project into actional pieces. \\n \\n &#8226; Set dues dates to make task active.";
    private String state = "Pending";
    private String dueDate = "01 Jan 2022";

    private boolean isOverdue = false;
    private boolean isInProgress = false;
    private boolean isDone = false;

    private int daysOverdue = 0;
    public CreateTasks () {
        this.task = "Create task";
        this.description = "Tasks are used to break down a project into actional pieces. \\n \\n &#8226; Set dues dates to make task active.";
        this.state = "Pending";
        this.dueDate = "01-Jan-2022";

        setOverdue();
    }

    public CreateTasks(String task, String project) {
        this.task = task;
        this.project = project;
        this.state = "Pending";
        this.dueDate = getCurrentDate();
        setOverdue();
    }

    public CreateTasks(String mProject, String mTask, String desc, String dateDue, String mState){
        this.project = mProject;
        this.task = mTask;
        this.description = desc;
        this.dueDate = dateDue;
        this.state = mState;

        setOverdue();

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

    public boolean isOverdue() {
        return isOverdue;
    }

    private void setOverdue() {
       Calendar today = Calendar.getInstance();

       SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dueDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar dueDate = Calendar.getInstance();
       dueDate.setTime(date);

       if(today.after(dueDate)){
           isOverdue = true;
       }
    }

    public int getDaysOverdue() {

        Calendar today = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(dueDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(date);

        int daysBetween = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            daysBetween = (int) ChronoUnit.DAYS.between(dueDate.toInstant(), today.toInstant());
        }
        return daysBetween;
    }
}