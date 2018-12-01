package com.example.utente.fotogram.Object_classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

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

        m.setActiveUserImage(encoded);
        return encoded;
    }

    public ImageView decodeString(String encoded){
        byte [] bytearr= Base64.decode(encoded, Base64.DEFAULT);
        Bitmap decoded= BitmapFactory.decodeByteArray(bytearr, 0, bytearr.length);

        image.setImageBitmap(decoded);
        return image;
    }

}
