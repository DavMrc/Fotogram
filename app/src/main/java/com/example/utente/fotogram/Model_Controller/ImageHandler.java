package com.example.utente.fotogram.Model_Controller;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

public abstract class ImageHandler {

    private static int QUALITY= 90;
    // non so perchè, ma 100 non funziona

    public static Bitmap decodeString(String encoded){
        if(encoded != null) {
            byte[] byteArr = Base64.decode(encoded, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        }else {
            return null;
        }
    }

    public static String fileToBase64(File file){
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos);
        byte[] byteArr = baos.toByteArray();

        return Base64.encodeToString(byteArr, Base64.DEFAULT);
    }
}
