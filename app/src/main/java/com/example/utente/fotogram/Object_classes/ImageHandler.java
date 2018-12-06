package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageHandler {
    private Context context;
    private Model m;
    private ImageView image;

    public ImageHandler(Context context) {
        this.context= context;
        m= Model.getInstance();
    }

    public String encodeFromUri(Uri uri){
        // la risorsa dev'essere raggiunta tramite drawable, altrimenti l'id punta al "container" della risorsa
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
        String encoded = Base64.encodeToString(byteArr, Base64.DEFAULT);

        m.setActiveUserImg(encoded);
        return encoded;
    }

    public Bitmap decodeString(String encoded){
        byte [] byteArr= Base64.decode(encoded, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
    }

}
