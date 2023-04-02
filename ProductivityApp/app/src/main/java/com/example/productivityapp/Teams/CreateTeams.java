package com.example.productivityapp.Teams;

import com.example.productivityapp.More.Post;

import java.util.ArrayList;

public class CreateTeams {

    private String teamName = "";

    private ArrayList<Post> members = new ArrayList<>();
    private String projectName = "";

    public CreateTeams() {
        this.teamName = "";
        this.members = new ArrayList<>();
        this.projectName = "";
    }
    public CreateTeams(String teamName) {
        this.teamName = teamName;
        this.members = new ArrayList<>();
        this.projectName = "";
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public ArrayList<Post> getMembers() {
        return this.members;
    }
    public void setMembers(ArrayList<Post> members) {
        this.members = members;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectName() {
        return projectName;
    }






}
