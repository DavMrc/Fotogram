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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RicercaFragment extends Fragment {

    private Context context;
    private Model m;
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
        m= Model.getInstance();
        serverService= ServerService.getInstance(context);

        final TextView output= v.findViewById(R.id.output);
        final TextView input= v.findViewById(R.id.txt_search_users);
        ImageButton search= v.findViewById(R.id.btn_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query= input.getText().toString();
                String out= "Users: ";

                //TODO: fix this sync bug
                ArrayList<User> users= serverService.searchUser(query);
                for(User u: users){
                    out= out.concat(u.getUsername()+", ");
                }

                output.setText(out);
            }
        });

        return v;
    }
}
