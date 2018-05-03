package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/8/18.
 * Model class
 */

class AllUsers {


    public String user_image;
    public String  username;
    public String expertise;


    // constructor
    public AllUsers(){

    }
    // constructor
    public AllUsers(String user_image, String username, String expertise) {
        this.user_image = user_image;
        this.username = username;
        this.expertise = expertise;
    }

    //getter and setter methods
    public String getUser_image() {
        return user_image;
    }

    //getter and setter methods
    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    //getter and setter methods
    public String getUsername() {
        return username;
    }

    //getter and setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    //getter and setter methods
    public String getExpertise() {
        return expertise;
    }

    //getter and setter methods
    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

}
