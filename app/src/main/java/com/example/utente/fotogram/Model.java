package com.example.utente.fotogram;

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

    public void setSessionID(String sessionID){
        this.sessionID= sessionID;
    }

    public String getSessionID(){
        return sessionID;
    }

    public void setActiveUserNickname(String username){
        activeUser= new User(username);
    }

    public String getActiveUserNickname(){
        return activeUser.getNickname();
    }

    public String getActiveUserImage(){
        return activeUser.getProfilePic();
    }

}
