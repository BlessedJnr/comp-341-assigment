package com.example.productivityapp.Project;

import java.util.ArrayList;

public class CreateProject {

    private String projectName;
    private ArrayList<CreateTasks> tasksList;
    private Boolean collaborated = false;
    private String mainOwner;

    public CreateProject(){
        this.projectName="";
        this.tasksList = new ArrayList<>();
    }
    public CreateProject(String projectName) {
        this.projectName = projectName;
        this.tasksList = new ArrayList<>();
    }

    public CreateProject(String projectName, String mainOwner) {
        this.projectName = projectName;
        this.mainOwner = mainOwner;
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

    public Boolean getCollaborated() {
        return collaborated;
    }

    public void setCollaborated(Boolean collaborated) {
        this.collaborated = collaborated;
    }

    public String getMainOwner() {
        return mainOwner;
    }

    public void setMainOwner(String mainOwner) {
        this.mainOwner = mainOwner;
    }
}
