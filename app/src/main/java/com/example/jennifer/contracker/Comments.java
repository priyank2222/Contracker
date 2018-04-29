package com.example.jennifer.contracker;

/**
 * Created by Jennifer on 4/22/2018.
 */

public class Comments {

    private String commentbody;
    private String senderusername;
    private String datestamp;

    // constructor
    public Comments() {


    }
    // constructor
    public Comments(String commentbody, String senderusername, String datestamp) {
        this.commentbody = commentbody;
        this.senderusername = senderusername;
        this.datestamp = datestamp;

    }

    //getter and setter methods
    public String getCommentbody() {
        return commentbody;
    }

    //getter and setter methods
    public void setCommentbody(String commentbody) {
        this.commentbody = commentbody;
    }

    //getter and setter methods
    public String getSenderusername() {
        return senderusername;
    }

    //getter and setter methods
    public void setSenderusername(String senderusername) {
        this.senderusername = senderusername;
    }

    //getter and setter methods
    public String getDatestamp(){ return datestamp; }

    //getter and setter methods
    public void setDatestamp(String datestamp){ this.datestamp = datestamp; }

}
