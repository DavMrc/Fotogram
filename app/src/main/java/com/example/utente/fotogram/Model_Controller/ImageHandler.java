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

    public static Bitmap decodeString(String encoded){
        if(encoded != null) {
            byte[] byteArr = Base64.decode(encoded, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        }else {
            return null;
        }
    }
}
