package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.SearchUsersAdapter;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.OthersProfile;
import com.example.utente.fotogram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RicercaFragment extends Fragment{

    private Context context;
    private Model m;
    private ServerService serverService;
    private ListView listView;

    public RicercaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_ricerca, container, false);

        context= getContext();
        m= Model.getInstance();
        serverService= ServerService.getInstance(context);

        final TextView input= v.findViewById(R.id.txt_search_users);
        listView= v.findViewById(R.id.query_suggestions);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query= s.toString();
                serverService.searchUser(RicercaFragment.this, query);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return v;
    }

    public void onPostServerRequest(final ArrayList<User> users) {
        SearchUsersAdapter adapter= new SearchUsersAdapter(context, R.layout.item_search_result_item, users);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String usernameClicked = users.get(position).getUsername();

                serverService.getOtherUserInfo(m.getSessionID(), usernameClicked);
            }
        });
    }
}
