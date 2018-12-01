package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfiloFragment extends Fragment {

    private static Model m;

    private static int PICK_PHOTO = 100;
    public static ImageView proPic;
    private static Context context;

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profilo, container, false);

        context= getContext();
        m= Model.getInstance();

        TextView tv_username= v.findViewById(R.id.txt_username);
        tv_username.setText( m.getActiveUserNickname() );

        proPic= v.findViewById(R.id.img_profile_pic);

        Button changeProPic = v.findViewById(R.id.btn_change_profile_pic);
        changeProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
                changeProfilePic();
            }
        });

        return v;
    }

    private void changeProfilePic(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO);
        //                     |
    }//                        |
//                             V
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_PHOTO & resultCode == Activity.RESULT_OK) {
            Uri imageURI = data.getData();

            if(imageURI !=null){
                proPic.setImageURI(imageURI);
                ServerService serverService= new ServerService(context);

//              miei metodi
                String encoded= encodeImage(imageURI);
                serverService.updatePicture(encoded, m.getSessionID());
            }
        }
    }

    public String encodeImage(Uri imageURI){
        // la risorsa dev'essere raggiunta tramite drawable, altrimenti l'id punta al "container" della risorsa
        String [] filePathColumn= {MediaStore.Images.Media.DATA};

        Cursor cursor= context.getContentResolver().query(imageURI, filePathColumn, null, null, null);
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

    private void checkStoragePermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }//    |
    }//        |
//             V
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                }else{
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
