package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.R;

import java.util.ArrayList;

public class BachecaFragment extends Fragment {
    private ArrayList<User> friends;

    private Model m;
    private Context context;
    private ImageHandler imageHandler;

    public BachecaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_bacheca, container, false);

        context= getContext();
        imageHandler= new ImageHandler(context);
        m= Model.getInstance();

        friends= m.getActiveUserFriends();

        return v;
    }
}
