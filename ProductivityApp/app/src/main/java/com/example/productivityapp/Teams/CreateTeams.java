package com.example.productivityapp.Teams;

import java.util.ArrayList;

public class CreateTeams {

    private String teamName = "";

    private ArrayList<String> members = new ArrayList<>();
    private ArrayList<String> projectNames = new ArrayList<>();

    public CreateTeams() {
        this.teamName = "";
        this.members = new ArrayList<>();
        this.projectNames = new ArrayList<>();
    }
    public CreateTeams(String teamName) {
        this.teamName = teamName;
        this.members = new ArrayList<>();
        this.projectNames = new ArrayList<>();
    }
    public CreateTeams(String teamName, ArrayList<String> members, ArrayList<String> projectNames) {
        this.teamName = teamName;
        this.members = members;
        this.projectNames = projectNames;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public ArrayList<String> getMembers() {
        return members;
    }
    public void setMembers(ArrayList<String> members) {
        members = members;
    }
    public void setProjectNames(ArrayList<String> projectNames) {
        this.projectNames = projectNames;
    }
    public ArrayList<String> getProjectNames() {
        return projectNames;
    }




}
