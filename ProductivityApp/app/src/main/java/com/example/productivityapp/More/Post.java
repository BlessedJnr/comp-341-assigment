package com.example.productivityapp.More;

public class Post {
    String name,email;

    public Post() {
        this.name = "";
        this.email = "";
    }

    public Post(String mEmail) {
        this.name = "";
        this.email = mEmail;
    }


    public Post(String name, String email) {
        this.name = name;
        this.email = email;
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
}
