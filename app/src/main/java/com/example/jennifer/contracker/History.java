package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/21/18.
 */

class History
{
    private String date;
    private String job_rating,job_title,job_description,job_location,service_category;


    // constructor
    public History(){

    }
    // constructor
    public History(String date, String job_rating, String job_title, String job_description, String job_location, String service_category) {
        this.date = date;
        this.job_rating = job_rating;
        this.job_title = job_title;
        this.job_description = job_description;
        this.job_location = job_location;
        this.service_category = service_category;
    }

    //getter and setter methods
    public String getDate() {
        return date;
    }

    //getter and setter methods
    public void setDate(String date) {
        this.date = date;
    }

    //getter and setter methods
    public String getJob_rating() {
        return job_rating;
    }

    //getter and setter methods
    public void setJob_rating(String job_rating) {
        this.job_rating = job_rating;
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
}
