package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
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
import com.example.utente.fotogram.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

public class Nuovo_Post_Fragment extends Fragment {

    private ImageView chooseImage;
    private ImageButton cancelPost;
    private Uri selectedImageUri;

    private Context context;
    private Model m;
    private static RequestQueue queue;

    private TextView tv_didascalia;
    private int POST_IMAGE_SIZE= 90;

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
        queue= Volley.newRequestQueue(context);

        tv_didascalia= v.findViewById(R.id.txt_didascalia);

        setListeners(v);

        return v;
    }

    private void setListeners(View v){
        // aggiunge un listener a tutto il CL
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
                encodeFromUri(selectedImageUri);
            }
        });
    }

    public void encodeFromUri(Uri uri){
        String [] filePathColumn= {MediaStore.Images.Media.DATA};

        Cursor cursor= context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex= cursor.getColumnIndex(filePathColumn[0]);
        String path= cursor.getString(columnIndex);
        cursor.close();

        File imageAsFile= new File(path);

        final String[] mString = new String[1];

        if(imageAsFile.length()/1024 > POST_IMAGE_SIZE){
            Luban.compress(context, imageAsFile).setMaxSize(POST_IMAGE_SIZE).putGear(Luban.CUSTOM_GEAR).launch(new OnCompressListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(File file) {
                    mString[0] = ImageHandler.fileToBase64(file);
                    sendImageToServer(mString[0]);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(context,
                            "Errore durante la compressione dell'immagine",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            mString[0] = ImageHandler.fileToBase64(imageAsFile);
            sendImageToServer(mString[0]);
        }
    }

    private void sendImageToServer(final String encoded){
        if(! isConnected()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }else {
            if (selectedImageUri != null) {
                final String didascalia = tv_didascalia.getText().toString();

                String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/create_post";

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    // risposta valida
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Immagine inviata al server correttamente", Toast.LENGTH_LONG).show();
                        // risetta la didascalia a nulla dopo la creazione del post
                        tv_didascalia.setText("");
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
            } else {
                Toast.makeText(context, "Immagine non valida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkStoragePermissions(){
//        permissions haven't been granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
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

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
