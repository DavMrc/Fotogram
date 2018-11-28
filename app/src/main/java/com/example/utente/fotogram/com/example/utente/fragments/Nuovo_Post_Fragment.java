package com.example.utente.fotogram.com.example.utente.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.utente.fotogram.R;

public class Nuovo_Post_Fragment extends Fragment {

    private static int PICK_PHOTO = 100;
    private ImageView chooseImage;

    public Nuovo_Post_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_nuovo__post_, container, false);

        chooseImage= v.findViewById(R.id.gallery_image);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_PHOTO);
            }
        });

        setConstraintLayoutListener(v);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_PHOTO & resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            if(selectedImage !=null){
                chooseImage.setImageURI(selectedImage);
            }
        }
    }

    private void setConstraintLayoutListener(View v){
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
    }

}
