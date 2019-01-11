package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.SearchUsersAdapter;
import com.example.utente.fotogram.Model_Controller.ServerService;
import com.example.utente.fotogram.Model_Controller.User;
import com.example.utente.fotogram.OthersProfile;
import com.example.utente.fotogram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RicercaFragment extends Fragment{

    private Context context;
    private Model m;
    private ListView listView;
    private static RequestQueue queue;

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
        queue= Volley.newRequestQueue(context);

        final TextView input= v.findViewById(R.id.txt_search_users);
        listView= v.findViewById(R.id.query_suggestions);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return v;
    }

    private void searchUser(final String query){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/users";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String serverResponse) {
                final ArrayList<User> users = parseSearchUsers(serverResponse);

                SearchUsersAdapter adapter= new SearchUsersAdapter(context, R.layout.item_search_result_item, users);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String clickedUserUsername = users.get(position).getUsername();

                        Intent intent= new Intent(context, OthersProfile.class);
                        intent.putExtra("username", clickedUserUsername);

                        context.startActivity(intent);
                    }
                });
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Impossibile effettuare ricerca", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("usernamestart", query);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private void getOtherUserInfo(final String otherUserUsername){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Impossibile ottenere informazioni utente", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("username", otherUserUsername);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private ArrayList<User> parseSearchUsers(String serverResponse){
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

        return users;
    }

}
