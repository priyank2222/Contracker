package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/20/18.
 */

public class Services
{
    private String date;
    private String hourly_rate;
    private String estimated_hour;

    public Services(){

    }

    //initiate services object - which is used for a contractor to bid on a service 
    public Services(String date, String hourly_rate, String estimated_hour) {
        this.date = date;
        this.hourly_rate = hourly_rate;
        this.estimated_hour = estimated_hour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHourly_rate() {
        return hourly_rate;
    }

    public void setHourly_rate(String hourly_rate) {
        this.hourly_rate = hourly_rate;
    }

    public String getEstimated_hour() {
        return estimated_hour;
    }

    public void setEstimated_hour(String estimated_hour) {
        this.estimated_hour = estimated_hour;
    }
}
