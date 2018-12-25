package com.example.utente.fotogram.Model_Controller;

public class Post {
    private String username;
    private String msg;
    private String img;
    private String timestamp;

    // costruttore
    public Post(String username, String msg, String img, String timestamp) {
        this.username = username;
        this.msg = msg;
        this.img = img;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
