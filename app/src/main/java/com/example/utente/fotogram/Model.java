package com.example.utente.fotogram;

import java.util.ArrayList;

class Model {
    private static Model instance = null;

    private Model() {}

    public static Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }
}
