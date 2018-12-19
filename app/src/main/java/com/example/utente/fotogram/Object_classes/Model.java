package com.example.utente.fotogram.Object_classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Model {
    //TODO: al boot dell'app, conviene scaricare la lista di amici da settare subito
    //TODO: imparare hashmap per associare utente -> imm profilo
    private static Model instance = null;

    private String sessionID;
    private String activeUsername;
    private String activePicture;

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

    public ArrayList<User> getSearchResultUsers() {
        ArrayList<User> result= new ArrayList<>();

        Log.d("DDD", "DDD Model search result: "+searchResultUsers);

        try {
            JSONObject jsonObject = new JSONObject(searchResultUsers);
            JSONArray array= jsonObject.getJSONArray("users");

            for(int i=0; i < array.length(); i++){
                JSONObject pointedUser= array.getJSONObject(i);
                String username= pointedUser.getString("name");
                String picture= pointedUser.getString("picture");

                result.add(new User(username, picture));
            }
            Log.d("DDD ","DDD/ Model arraylist: "+result);
            return result;
        }catch (JSONException e){
            e.printStackTrace();
        }

        return result;
    }

    public void setSearchResultUsers(String searchResultUsers) {
        Log.d("DDD", "DDD search result: "+searchResultUsers);
        this.searchResultUsers = searchResultUsers;
    }

}
