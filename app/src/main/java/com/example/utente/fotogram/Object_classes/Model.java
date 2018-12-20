package com.example.utente.fotogram.Object_classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Model {
    //TODO: imparare hashmap per associare utente -> imm profilo
    private static Model instance = null;

    private String sessionID;
    private String activeUsername;
    private String activePicture;
    private Post [] activePosts= new Post[10];
    private ArrayList<User> friends;

    private String searchResultUsers;

    private Model() {}

    public static synchronized Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    public String getSessionID(){
        return sessionID;
    }

    public void setSessionID(String sessionID){
        this.sessionID= sessionID;
    }

    public String getActiveUserNickname(){
        return activeUsername;
    }

    public void setActiveUserNickname(String username){
        this.activeUsername= username;
    }

    public String getActiveUserImg(){
        return activePicture;
    }

    public void setActiveUserImg(String encodedImg){
        this.activePicture= encodedImg;
    }

    public Post [] getActivePosts(){
        return this.activePosts;
    }

    public void setActivePosts(Post [] posts){
        this.activePosts= posts;
    }

    public ArrayList<User> getFriends(){
        return this.friends;
    }

    public void setFriends(ArrayList<User> friends){
        this.friends= friends;
    }

}
