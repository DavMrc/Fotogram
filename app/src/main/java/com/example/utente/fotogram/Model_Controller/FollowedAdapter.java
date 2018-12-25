package com.example.utente.fotogram.Model_Controller;

import android.content.Context;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FollowedAdapter extends Ada {
    private Context context;
    private HashMap<String, String> users;
    private ImageHandler imageHandler;

    public SearchUsersAdapter(Context context, int resource, HashMap<String, String> users) {
        super(context, resource, users);
        this.context= context;
        this.users= users;

        imageHandler= new ImageHandler(context);
    }
}
