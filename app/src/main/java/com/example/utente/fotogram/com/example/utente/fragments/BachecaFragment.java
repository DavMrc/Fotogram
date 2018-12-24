package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.Object_classes.BachecaPostsAdapter;
import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BachecaFragment extends Fragment {
    private HashMap<String, String> friends;

    private Model m;
    private Context context;
    private ImageHandler imageHandler;
    private ServerService serverService;

    private ListView postListView;
    private ProgressBar progressBar;

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
        serverService= ServerService.getInstance(context);
        m= Model.getInstance();

        postListView= v.findViewById(R.id.bacheca_posts);
        progressBar= v.findViewById(R.id.wall_progress_bar);
        friends= m.getActiveUserFriends();

        serverService.wall(BachecaFragment.this, m.getSessionID());

        return v;
    }

    // richiamato da ServerService.wall
    public void getPosts(){
        BachecaPostsAdapter adapter= new BachecaPostsAdapter(
                context,
                R.layout.item_bacheca_post_item,
                m.getWall()
        );
        postListView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    // chiamato da ServerService
    public void onRefreshUserInfo(){
        ((Navigation)getActivity()).stopRefreshAnimation();
    }
}
