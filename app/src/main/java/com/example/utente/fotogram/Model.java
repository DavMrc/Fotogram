package com.example.utente.fotogram;

class Model {
    private static Model instance = null;

    private Model() {}

    static Model getInstance() {
        if(instance == null){
            instance = new Model();
        }
        return instance;
    }
}
