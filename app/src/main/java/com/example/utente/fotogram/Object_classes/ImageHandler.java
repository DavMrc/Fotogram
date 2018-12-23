package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageHandler {
    private Context context;
    private Model m;

    public ImageHandler(Context context) {
        this.context= context;
        m= Model.getInstance();
    }

    public String encodeFromUri(Uri uri){
        String [] filePathColumn= {MediaStore.Images.Media.DATA};

        Cursor cursor= context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex= cursor.getColumnIndex(filePathColumn[0]);
        String path= cursor.getString(columnIndex);
        cursor.close();

        final Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArr = baos.toByteArray();

        return Base64.encodeToString(byteArr, Base64.DEFAULT);
    }

    public Bitmap decodeString(String encoded){
        if(encoded != null) {
            byte[] byteArr = Base64.decode(encoded, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        }else {
            return null;
        }
    }

}
