package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;
import com.example.utente.fotogram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RicercaFragment extends Fragment{

    private Context context;
    private Model m;
    private ServerService serverService;
    private TextView output;

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
        output = v.findViewById(R.id.output);


        final TextView input= v.findViewById(R.id.txt_search_users);
        ImageButton search= v.findViewById(R.id.btn_search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query= input.getText().toString();

                serverService.searchUser(RicercaFragment.this, query);
            }
        });

        return v;
    }

    public void onPostRequest(String serverResponse) {
        String out= "Users: ";
        ArrayList<User> users= new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONArray array= jsonObject.getJSONArray("users");

            for(int i=0; i < array.length(); i++){
                JSONObject pointedUser= array.getJSONObject(i);
                String username= pointedUser.getString("name");
                String picture= pointedUser.getString("picture");

                users.add(new User(username, picture));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        for(User u: users){
            out= out.concat(u.getUsername()+", ");
        }

        output.setText(out);
    }
}
