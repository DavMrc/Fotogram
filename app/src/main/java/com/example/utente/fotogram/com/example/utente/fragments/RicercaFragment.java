package com.example.utente.fotogram.com.example.utente.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RicercaFragment extends Fragment {

    public SearchView searchView;

    private Context context;
    private ServerService serverService;

    public RicercaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_ricerca, container, false);

        context= getContext();
        serverService= new ServerService(context);

        searchView= v.findViewById(R.id.search_users);
        searchView.setIconifiedByDefault(false);

        TextView textView= v.findViewById(R.id.output);

        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<User> users= serverService.searchUser(query);

            String t= "Users: ";
            for(User u: users){
                t= t+ u.getUsername()+", ";
            }

            textView.setText(t);
        }


        return v;
    }
}
