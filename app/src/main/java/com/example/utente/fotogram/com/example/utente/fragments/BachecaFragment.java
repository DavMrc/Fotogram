package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Model_Controller.BachecaPostsAdapter;
import com.example.utente.fotogram.Model_Controller.ImageHandler;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.Post;
import com.example.utente.fotogram.Model_Controller.ServerService;
import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BachecaFragment extends Fragment {
    private HashMap<String, String> friends;

    private Model m;
    private Context context;
    private ImageHandler imageHandler;
    private static RequestQueue queue;
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
        m= Model.getInstance();
        queue= Volley.newRequestQueue(context);
        serverService= ServerService.getInstance(context);

        postListView= v.findViewById(R.id.bacheca_posts);
        progressBar= v.findViewById(R.id.wall_progress_bar);
//        friends= m.getActiveUserFriends();

        getWall();

        return v;
    }

    public void getWall(){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/wall";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                Post[] wall= parseWall(response);

                BachecaPostsAdapter adapter= new BachecaPostsAdapter(
                        context,
                        R.layout.item_bacheca_post_item,
                        wall
                );
                postListView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Impossibile scaricare bacheca", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private Post[] parseWall(String serverResponse){
        Post [] posts= new Post[10];

        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONArray array= jsonObject.getJSONArray("posts");

            for(int i=0; i < array.length(); i++){
                JSONObject pointedPost= array.getJSONObject(i);

                String didascalia= pointedPost.getString("msg");
                String img= pointedPost.getString("img");
                String user= pointedPost.getString("user");
                String timestamp= pointedPost.getString("timestamp");

                posts[i]= new Post(user, didascalia, img, timestamp);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return posts;
    }

}
