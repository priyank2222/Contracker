package com.example.jennifer.contracker;

/**
 * Created by kezhao on 4/17/18.
 * Model class
 */

public class Messages
{
    private String message, type;
    private long time;
    private boolean seen;
    private String from;

    // constructor
    public Messages() {

    }

    // constructor
    public Messages(String message, String type, long time, boolean seen, String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    //getter and setter methods
    public String getMessage() {
        return message;
    }

    //getter and setter methods
    public void setMessage(String message) {
        this.message = message;
    }

    //getter and setter methods
    public String getType() {
        return type;
    }

    //getter and setter methods
    public void setType(String type) {
        this.type = type;
    }

    //getter and setter methods
    public long getTime() {
        return time;
    }

    //getter and setter methods
    public void setTime(long time) {
        this.time = time;
    }

    //getter and setter methods
    public boolean isSeen() {
        return seen;
    }

    //getter and setter methods
    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    //getter and setter methods
    public String getFrom() {
        return from;
    }

    //getter and setter methods
    public void setFrom(String from) {
        this.from = from;
    }
}
