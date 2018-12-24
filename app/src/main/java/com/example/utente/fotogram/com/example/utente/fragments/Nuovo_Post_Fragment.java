package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.Post;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Nuovo_Post_Fragment extends Fragment {

    private ImageView chooseImage;
    private ImageButton cancelPost;
    private Uri selectedImageUri;

    private Context context;
    private Model m;
    private ImageHandler imageHandler;
    private ServerService serverService;
    private Post post;

    private TextView tv_didascalia;

    public Nuovo_Post_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_nuovo__post_, container, false);

        m= Model.getInstance();
        context= getContext();
        imageHandler= new ImageHandler(context);
        serverService= ServerService.getInstance(context);

        tv_didascalia= v.findViewById(R.id.txt_didascalia);

        setListeners(v);

        return v;
    }

    private void setListeners(View v){
        // aggiunge un listener a tutto il CL in modo da chiudere
        // la tastiera quando si fa click ovunque
        final ConstraintLayout constraintLayout = v.findViewById(R.id.constraint_layout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
            }
        });

        // bottone rosso per cancellare l'immagine
        cancelPost= v.findViewById(R.id.btn_cancel_post);
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage.setImageResource(R.drawable.ic_insert_photo_blue_grey_500_48dp);

                // lo nasconde
                cancelPost.setVisibility(View.GONE);
            }
        });


        // bottone per scegliere l'immagine
        chooseImage= v.findViewById(R.id.gallery_image);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
            }
        });


        // bottone per inviare il post
        final ImageButton createPost= v.findViewById(R.id.btn_create_post);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageToServer();
            }
        });
    }

    // TODO: non è ben sincronizzato: non compare la scritta E POI ti fa partire subito l'intent
    private void checkStoragePermissions(){
//        permissions haven't been granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
//        permissions have been granted, proceed
            addImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addImage();
                } else {
                    Toast.makeText(context, "Permesso negato", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void addImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturePath= pictureDirectory.getPath();

        Uri actualPictures= Uri.parse(picturePath);
        photoPickerIntent.setDataAndType(actualPictures,"image/*");
        startActivityForResult(photoPickerIntent, 20);
    }

    @Override
    // relativo al photoPickerIntent, serve per aggiunge l'immagine
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 20 & resultCode == Activity.RESULT_OK) {

            selectedImageUri = data.getData();

            if(selectedImageUri != null){
                chooseImage.setImageURI(selectedImageUri);

                // mostra il bottone "delete"
                cancelPost.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(context, "Impossibile caricare immagine", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendImageToServer(){
        String didascalia= tv_didascalia.getText().toString();
        if(selectedImageUri != null) {
            String encoded = imageHandler.encodeFromUri(selectedImageUri);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms").format(new Date());

            post= new Post(m.getActiveUserNickname(), didascalia, encoded, timeStamp);

            serverService.createPost(m.getSessionID(), post);
            // risetta la didascalia a nulla dopo la creazione del post
            tv_didascalia.setText("");
        }else{
            Toast.makeText(context, "Immagine non valida", Toast.LENGTH_SHORT).show();
        }
    }
}
