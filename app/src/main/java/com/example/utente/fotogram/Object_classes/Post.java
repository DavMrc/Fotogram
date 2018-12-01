package com.example.utente.fotogram.Object_classes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Post {
    private String creatore;
    private String didascalia;
    private String encoded;
    private Bitmap bitmap;

    public Post(String creatore, String didascalia) {
        this.creatore = creatore;
        this.didascalia = didascalia;
    }

    public String getCreatore() {
        return creatore;
    }

    public void setCreatore(String creatore) {
        this.creatore = creatore;
    }

    public String getDidascalia() {
        return didascalia;
    }

    public void setDidascalia(String didascalia) {
        this.didascalia = didascalia;
    }


}
