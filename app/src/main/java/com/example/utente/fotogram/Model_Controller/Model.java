package com.example.utente.fotogram.Model_Controller;

import java.util.HashMap;

public class Model {
    private static Model instance = null;

    private String sessionID;
    private String username;
    private HashMap<String, String> friends;

    private User activeUser;
    private User otherUser; // utente su cui si è fatto click dopo ricerca

    private Model() {}

    public static synchronized Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

//    public User getActiveUser(){
//        return activeUser;
//    }
//
//    public void setActiveUser(User user){
//        this.activeUser= user;
//    }

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
        this.friends.replace(username, image);
    }

//    public String getActiveUserNickname(){
//        return activeUser.getUsername();
//    }
//
//    public void setActiveUserNickname(String username){
//        activeUser.setUsername(username);
//    }
//
//    public String getActiveUserImg(){
//        return activeUser.getImg();
//    }
//
//    public void setActiveUserImg(String encodedImg){
//        activeUser.setImg(encodedImg);
//    }

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
