package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/8/18.
 */

class AllUsers {


    public String user_image;
    public String  username;
    public String expertise;

    public AllUsers(){

    }

    public AllUsers(String user_image, String username, String expertise) {
        this.user_image = user_image;
        this.username = username;
        this.expertise = expertise;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

}
