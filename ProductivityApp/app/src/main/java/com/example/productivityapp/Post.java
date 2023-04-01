package com.example.productivityapp;

public class Post {
    String name,email,team,project;

    public Post() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Post{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", team='" + team + '\'' +
                ", project='" + project + '\'' +
                '}';
    }
}
