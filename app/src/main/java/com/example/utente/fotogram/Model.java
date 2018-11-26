package com.example.utente.fotogram;

import java.util.ArrayList;

class Model {
    private static Model instance = null;
    private String sessionID;

    private Model() {}

    public static Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }

    public void setActiveUser(String sessionID){
        this.sessionID= sessionID;
    }
}
