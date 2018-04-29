package com.example.jennifer.contracker;

/**
 * Created by Jennifer on 4/22/2018.
 */

public class Comments {

    private String commentbody;
    private String senderusername;
    private String datestamp;


    public Comments() {


    }

    public Comments(String commentbody, String senderusername, String datestamp) {
        this.commentbody = commentbody;
        this.senderusername = senderusername;
        this.datestamp = datestamp;

    }

    public String getCommentbody() {
        return commentbody;
    }

    public void setCommentbody(String commentbody) {
        this.commentbody = commentbody;
    }

    public String getSenderusername() {
        return senderusername;
    }

    public void setSenderusername(String senderusername) {
        this.senderusername = senderusername;
    }

    public String getDatestamp(){ return datestamp; }

    public void setDatestamp(String datestamp){ this.datestamp = datestamp; }

}
