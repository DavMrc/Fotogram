package com.example.utente.fotogram.Object_classes;

import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    private static Model instance = null;

    private String sessionID;
    private User activeUser;
    private User otherUser; // utente su cui si è fatto click dopo ricerca

    private Model() {}

    public static synchronized Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    public User getActiveUser(){
        return activeUser;
    }

    public void setActiveUser(User user){
        this.activeUser= user;
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
        activeUser.setUsername(username);
    }

    public String getActiveUserImg(){
        return activeUser.getImg();
    }

    public void setActiveUserImg(String encodedImg){
        activeUser.setImg(encodedImg);
    }

    public Post [] getActivePosts(){
        return activeUser.getPosts();
    }

    public void setActivePosts(Post [] posts){
        activeUser.setPosts(posts);
    }

    public HashMap<String, String> getActiveUserFriends(){
        return activeUser.getFriends();
    }

    public void setActiveUserFriends(HashMap<String, String> friends){
       activeUser.setFriends(friends);
    }

    public void addFriend(String username, String img){
       if(! activeUser.getFriends().containsKey(username) ){
           activeUser.addFriend(username, img);
       }
    }

    public void removeFriend(String username){
        activeUser.removeFriend(username);
    }

    public User getOtherUser(){
        return this.otherUser;
    }

    public void setOtherUser(User user){
        this.otherUser= user;
    }

}
