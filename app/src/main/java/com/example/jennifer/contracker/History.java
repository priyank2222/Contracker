package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/21/18.
 */

class History
{
    private String date;
    private String job_rating,job_title,job_description,job_location,service_category;



    public History(){

    }

    public History(String date, String job_rating, String job_title, String job_description, String job_location, String service_category) {
        this.date = date;
        this.job_rating = job_rating;
        this.job_title = job_title;
        this.job_description = job_description;
        this.job_location = job_location;
        this.service_category = service_category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJob_rating() {
        return job_rating;
    }

    public void setJob_rating(String job_rating) {
        this.job_rating = job_rating;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public String getJob_location() {
        return job_location;
    }

    public void setJob_location(String job_location) {
        this.job_location = job_location;
    }

    public String getService_category() {
        return service_category;
    }

    public void setService_category(String service_category) {
        this.service_category = service_category;
    }
}
