package com.example.utente.fotogram.Object_classes;

import java.util.ArrayList;

public class Model {
    private static Model instance = null;

    private String sessionID;
    private String activeUsername;
    private String activePicture;
    private Post [] activePosts= new Post[10];
    private ArrayList<User> activeUserFriends;
    private User otherUser; // utente su cui si è fatto click dopo ricerca

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

    public ArrayList<User> getActiveUserFriends(){
        return this.activeUserFriends;
    }

    public void setActiveUserFriends(ArrayList<User> activeUserFriends){
        this.activeUserFriends = activeUserFriends;
    }

    public void addFriend(User user){
        boolean areEquals= contiene(user);

//        if there are no matches, add it
        if(! areEquals){
            activeUserFriends.add(user);
        }
    }

    public boolean contiene(User user){
        boolean areEquals= false;

        for(User u: activeUserFriends){
            if( u.getUsername().equals(user.getUsername()) ){
                areEquals= true;
            }
        }

        return areEquals;
    }

    public void emptyFriends(){
        activeUserFriends.clear();
    }

    public User getOtherUser(){
        return this.otherUser;
    }

    public void setOtherUser(User user){
        this.otherUser= user;
    }

}
