package com.example.utente.fotogram.com.example.utente.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.Post;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Nuovo_Post_Fragment extends Fragment {

    private static int PICK_PHOTO = 100;
    private ImageView chooseImage;
    private ImageButton cancelPost;
    private ImageButton refreshPost;
    private Uri selectedImageUri;

    private Context context;
    private Model m;
    private ImageHandler imageHandler;
    private ServerService serverService;
    private Post post;

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
        serverService= new ServerService(context);

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
        // bottone per cambiare l'immagine
        refreshPost= v.findViewById(R.id.btn_change_post);
        refreshPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_PHOTO);
            }
        });
        // bottone per cancellare l'immagine
        cancelPost= v.findViewById(R.id.btn_cancel_post);
        cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage.setImageResource(R.drawable.ic_insert_photo_blue_grey_500_48dp);

                // nasconde i bottoni
                cancelPost.setVisibility(View.GONE);
                refreshPost.setVisibility(View.GONE);
            }
        });
        // bottone per scegliere l'immagine
        chooseImage= v.findViewById(R.id.gallery_image);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_PHOTO);
            }
        });

        final TextView tv_didascalia= v.findViewById(R.id.txt_didascalia);
        // bottone per inviare il post
        ImageButton createPost= v.findViewById(R.id.btn_create_post);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String didascalia= tv_didascalia.getText().toString();
                if(selectedImageUri != null) {
                    String encoded = imageHandler.encodeFromUri(selectedImageUri);
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ms").format(new Date());

                    post= new Post(m.getActiveUserNickname(), didascalia, encoded, timeStamp);

                    serverService.createPost(post);
                }else{
                    Toast.makeText(context, "Immagine non valida", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    // relativo al photoPickerIntent, serve per aggiunge l'immagine
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_PHOTO & resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
            if(selectedImageUri !=null){
                chooseImage.setImageURI(selectedImageUri);
            }

            // mostra i bottoni "cambia" e "delete"
            cancelPost.setVisibility(View.VISIBLE);
            refreshPost.setVisibility(View.VISIBLE);
        }
    }
}
