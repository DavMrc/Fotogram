package com.example.utente.fotogram;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;

public class Model {
    private static Model instance = null;
    private String sessionID;
    private User activeUser;

    private Model() {}

    public static Model getInstance() {
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
        return activeUser.getNickname();
    }

    public void setActiveUserNickname(String username){
        activeUser= new User(username);
    }

    public String getActiveUserImage(){
        return activeUser.getProfilePic();
    }

    public void setActiveUserImage(String s){
        activeUser.setProfilePic(s);
    }

}
