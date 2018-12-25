package com.example.utente.fotogram.Model_Controller;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String img;
    private Post [] posts= new Post[10];
    private HashMap<String, String> friends;

//    costruttore
    public User(String username, String img){
        this.username= username;
        this.img= img;
    }
//    costruttore completo per le SharedPreferences
    public User(String username, String img, Post [] posts, HashMap<String, String> friends){
        this.username= username;
        this.img= img;
        this.posts= posts;
        this.friends= friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Post[] getPosts() {
        return posts;
    }

    public void setPosts(Post[] posts) {
        this.posts = posts;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }

    public void addFriend(String username, String img){
        this.friends.put(username, img);
    }

    public void removeFriend(String username){
        this.friends.remove(username);
    }

}
