package com.example.utente.fotogram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Model_Controller.ImageHandler;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.Post;
import com.example.utente.fotogram.Model_Controller.ProfilePostsAdapter;
import com.example.utente.fotogram.Model_Controller.ServerService;
import com.example.utente.fotogram.Model_Controller.User;

import java.util.HashMap;
import java.util.Map;

public class OthersProfile extends AppCompatActivity {

    private String username;
    private String img;
    private Post[] posts;

    private Model m;
    private ImageHandler imageHandler;
    private static RequestQueue queue;

    private Button followButton;
    private ListView postListView;

    private boolean is_self_profile;
    private boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        Intent intent= getIntent();

        m= Model.getInstance();
        imageHandler= new ImageHandler(this);
        queue= Volley.newRequestQueue(this);
        username= intent.getStringExtra("username");

        getUserInfo();

        is_self_profile = username.equals(m.getUsername());
        following= m.getActiveUserFriends().containsKey(username);

        getSupportActionBar().setTitle(username);

        // img profilo
        ImageView profilePic= findViewById(R.id.img_profile_pic);
        Bitmap bitmap= imageHandler.decodeString(otherUser.getImg());
        if(bitmap != null) {
            profilePic.setImageBitmap(bitmap);
        }

        // username
        TextView tv_username= findViewById(R.id.txt_username);
        tv_username.setText(username);

        //TODO: gli altri utenti non hanno una loro lista di amici?

        // lista post
        postListView= findViewById(R.id.others_posts);
        getPosts();

        // bottone segui
        followButton= findViewById(R.id.btn_segui);

        if(is_self_profile){
            followButton.setText(R.string.follow_yourself);
        }else if( following ){
            followButton.setBackgroundResource(R.drawable.light_blue_8dp_radius_button);
            followButton.setTextColor(getResources().getColor(R.color.white));
            followButton.setText(R.string.following);
        }else{
            followButton.setBackgroundResource(R.drawable.button_gray_outline);
            followButton.setTextColor(getResources().getColor(R.color.black));
            followButton.setText(R.string.not_following);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollow();
            }
        });
    }

    private void getUserInfo(){

    }

    private void followUnfollow() {
        if( is_self_profile ){
            Toast.makeText(this, R.string.follow_yourself, Toast.LENGTH_SHORT).show();
        }else if(! following ) {
            followButton.setBackgroundResource(R.drawable.light_blue_8dp_radius_button);
            followButton.setTextColor(getResources().getColor(R.color.white));
            followButton.setText(R.string.following);

            followServerCall();
            Toast.makeText(this,
                    "Hai iniziato a seguire " + username,
                    Toast.LENGTH_SHORT).show();

            following= ! following;
        }else{
            followButton.setBackgroundResource(R.drawable.button_gray_outline);
            followButton.setTextColor(getResources().getColor(R.color.black));
            followButton.setText(R.string.not_following);

            unfollowServerCall();
            Toast.makeText(this,
                    "Non segui più "+ username,
                    Toast.LENGTH_SHORT).show();

            following= ! following;
        }
    }

    private void followServerCall(){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/follow";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.addFriend(username, img);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(OthersProfile.this, "Impossibile seguire", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("username", username);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private void unfollowServerCall(){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/unfollow";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.removeFriend(username);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(OthersProfile.this, "Impossibile smettere di seguire", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("username", username);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private void getPosts(){
        ProfilePostsAdapter adapter = new ProfilePostsAdapter(
                this,
                R.layout.item_user_posts_item,
                otherUser.getPosts());
        postListView.setAdapter(adapter);
    }

}
