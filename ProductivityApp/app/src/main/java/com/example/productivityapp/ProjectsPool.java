package com.example.productivityapp;

import java.util.ArrayList;
import java.util.List;

public class ProjectsPool {
    public static List<Project> projects = new ArrayList<>();

    public ProjectsPool() {

        projects.add(new Project("Banking", false));
        projects.add(new Project("Cooking", false));
        projects.add(new Project("Eating", false));
        projects.add(new Project("Dance", false));
        projects.add(new Project("Netflix", false));
        projects.add(new Project("Dipatje", false));

        for(Project project : projects) {
            project.setTasks(populateTasks(project));
        }

    }

    private List<Task> populateTasks(Project proj){
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task for "+ proj.getName(),"21-aug-22", Task.DONE));
        tasks.add(new Task("Task for "+ proj.getName(),"21-aug-22", Task.PENDING));
        tasks.add(new Task("Task for "+ proj.getName(),"21-aug-22", Task.PENDING));
        tasks.add(new Task("Task for "+ proj.getName(),"21-aug-22", Task.IN_PROGRESS));

        return  tasks;
    }
}

