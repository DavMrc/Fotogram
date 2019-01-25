package com.example.utente.fotogram.Model_Controller;

import java.util.HashMap;

public class Model {
    private static Model instance = null;

    private String sessionID;
    private String username;
    private HashMap<String, String> friends;

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

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username= username;
    }

    public String getImage(String username){
        return this.friends.get(username);
    }

    public void setImage(String image){
        try{
            this.friends.replace(username, image);
        }catch (Exception e){
            // friends non è ancora stato settato, readSharedPref tenta di fare un replace
            // ma trova la hashmap nulla. procedi alla bacheca, che setterà la hashmap
        }
    }

    public HashMap<String, String> getActiveUserFriends(){
        return friends;
    }

    public void setActiveUserFriends(HashMap<String, String> friends){
       this.friends= friends;
    }

    public void addFriend(String username, String img){
        this.friends.put(username, img);
    }

    public void removeFriend(String username){
        this.friends.remove(username);
    }
}