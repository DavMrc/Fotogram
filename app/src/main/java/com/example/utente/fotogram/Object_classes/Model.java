package com.example.utente.fotogram.Object_classes;

public class Model {
    private static Model instance = null;
    private String sessionID;
    private User activeUser;

    private String json;

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
        return activeUser.getUsername();
    }

    public void setActiveUserNickname(String username){
        activeUser= new User(username);
    }

    public String getActiveUserImage(){
        return activeUser.getPicture();
    }

    public void setActiveUserImage(String s){
        activeUser.setPicture(s);
    }

    public String getJsonDebug(){
        return json;
    }

    public void setJsonDebug(String json){
        this.json= json;
    }

}
