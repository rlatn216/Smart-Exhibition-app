package com.example.e_v1;

public class title_and_email {
    String title;
    String email;

    title_and_email(){}

    title_and_email(String title, String email){
        this.title = title;
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
