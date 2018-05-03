package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/18/18.
 * Model class
 */

public class Bids
{
    private String estimated_hour,hourly_rate,request_type;

    // constructor
    public Bids(){

    }
    // constructor
    public Bids(String estimated_hour, String hourly_rate, String request_type) {
        this.estimated_hour = estimated_hour;
        this.hourly_rate = hourly_rate;
        this.request_type = request_type;

    }
    //getter and setter methods
    public String getEstimated_hour() {
        return estimated_hour;
    }

    //getter and setter methods
    public void setEstimated_hour(String estimated_hour) {
        this.estimated_hour = estimated_hour;
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
    public String getRequest_type() {
        return request_type;
    }

    //getter and setter methods
    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
