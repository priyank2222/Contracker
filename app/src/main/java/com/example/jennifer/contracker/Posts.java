package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/11/18.
 * Model class
 */

public class Posts {

    public String job_title;
    public String job_description;
    public String job_location;
    public String service_category;
    public String close_bidding;


    public Posts(){

    }

    public Posts(String job_title, String job_description, String job_location, String service_category, String close_bidding) {
        this.job_title = job_title;
        this.job_description = job_description;
        this.job_location = job_location;
        this.service_category = service_category;
        this.close_bidding = close_bidding;
    }


    //getter and setter methods
    public String getJob_title() {
        return job_title;
    }

    //getter and setter methods
    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    //getter and setter methods
    public String getJob_description() {
        return job_description;
    }

    //getter and setter methods
    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    //getter and setter methods
    public String getJob_location() {
        return job_location;
    }

    //getter and setter methods
    public void setJob_location(String job_location) {
        this.job_location = job_location;
    }

    //getter and setter methods
    public String getService_category() {
        return service_category;
    }

    //getter and setter methods
    public void setService_category(String service_category) {
        this.service_category = service_category;
    }

    //getter and setter methods
    public String getClose_bidding() {
        return close_bidding;
    }

    //getter and setter methods
    public void setClose_bidding(String close_bidding) {
        this.close_bidding = close_bidding;
    }
}
