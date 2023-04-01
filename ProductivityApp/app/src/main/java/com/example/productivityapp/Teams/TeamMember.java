package com.example.productivityapp.Teams;

public class TeamMember {

    String name,email,team,project;

    public TeamMember(String name, String email, String team, String project) {
        this.name = name;
        this.email = email;
        this.team = team;
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTeam() {
        return team;
    }

    public String getProject() {
        return project;
    }
}
