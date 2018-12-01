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

//    public void createBitmap(int resource){
//        this.bitmap = BitmapFactory.decodeResource(Resources.getSystem(), resource);
//
//        new AsyncTask<Void, Void, String>(){
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                ByteArrayOutputStream baos= new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte [] byteArr= baos.toByteArray();
//
//                String encoded= Base64.encodeToString(byteArr, Base64.DEFAULT);
//                return encoded;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
////                super.onPostExecute(s);
//                Log.d("DDD", "Encoded string: "+s);
//            }
//        }.execute();
//    }
}
