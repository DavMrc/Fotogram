package com.example.utente.fotogram.Object_classes;

import android.graphics.Bitmap;

public class Post {
    private String username;
    private String msg;
    private String picture;
    private String timestamp;

    // costruttore
    public Post(String username, String msg, String picture, String timestamp) {
        this.username = username;
        this.msg = msg;
        this.picture= picture;
        this.timestamp= timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
