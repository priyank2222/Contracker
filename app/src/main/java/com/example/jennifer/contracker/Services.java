package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/20/18.
 * Model class
 */

public class Services
{
    private String date;
    private String hourly_rate;
    private String estimated_hour;

    // constructor
    public Services(){

    }

    // constructor
    public Services(String date, String hourly_rate, String estimated_hour) {
        this.date = date;
        this.hourly_rate = hourly_rate;
        this.estimated_hour = estimated_hour;
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
    public String getHourly_rate() {
        return hourly_rate;
    }

    //getter and setter methods
    public void setHourly_rate(String hourly_rate) {
        this.hourly_rate = hourly_rate;
    }

    //getter and setter methods
    public String getEstimated_hour() {
        return estimated_hour;
    }

    //getter and setter methods
    public void setEstimated_hour(String estimated_hour) {
        this.estimated_hour = estimated_hour;
    }
}
