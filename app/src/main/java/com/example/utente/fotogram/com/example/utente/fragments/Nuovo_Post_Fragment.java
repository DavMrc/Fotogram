package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.utente.fotogram.Model_Controller.ImageHandler;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.Post;
import com.example.utente.fotogram.Model_Controller.ServerService;
import com.example.utente.fotogram.R;

import java.io.File;
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
    private static RequestQueue queue;
    private Post post;

    private TextView tv_didascalia;

    public Nuovo_Post_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_nuovo__post, container, false);

        m= Model.getInstance();
        context= getContext();
        imageHandler= new ImageHandler(context);
        queue= Volley.newRequestQueue(context);

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

    private void checkStoragePermissions(){
//        permissions haven't been granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
//        permissions have been previously granted, proceed
            addImageFromGallery();
        }
    }

    public void addImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturePath= pictureDirectory.getPath();

        Uri actualPictures= Uri.parse(picturePath);
        photoPickerIntent.setDataAndType(actualPictures,"image/*");
        startActivityForResult(photoPickerIntent, 20);
    }

    private void sendImageToServer(){
        if(selectedImageUri != null) {
            final String didascalia= tv_didascalia.getText().toString();
            final String encoded = imageHandler.encodeFromUri(selectedImageUri);
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms").format(new Date());

//           ---------------------START REQUEST--------------------

            String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/create_post";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                // risposta valida
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Immagine inviata al server correttamente", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                // risposta ad un errore
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(context, "Impossibile inviare immagine al server", Toast.LENGTH_LONG).show();
                }
            }) {
                // parametri richiesta POST
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("session_id", m.getSessionID());
                    params.put("img", encoded);
                    params.put("message", didascalia);

                    return params;
                }
            };// finisce la StringRequest

            queue.add(request);

//            ---------------------END REQUEST----------------------

            // risetta la didascalia a nulla dopo la creazione del post
            tv_didascalia.setText("");
        }else{
            Toast.makeText(context, "Immagine non valida", Toast.LENGTH_SHORT).show();
        }
    }

}
