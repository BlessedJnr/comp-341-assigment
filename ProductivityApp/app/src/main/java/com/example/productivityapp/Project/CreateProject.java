package com.example.productivityapp.Project;

import java.util.ArrayList;

public class CreateProject {

    private String projectName;
    private ArrayList<CreateTasks> tasksList;

    public CreateProject(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<CreateTasks> getTasksList() {
        return tasksList;
    }

    public void setTasksList(ArrayList<CreateTasks> tasksList) {
        this.tasksList = tasksList;
    }
}
