package com.example.utente.fotogram;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.example.utente.fotogram.Model_Controller.ProfilePostsAdapter;
import com.example.utente.fotogram.Model_Controller.User;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class OthersProfile extends AppCompatActivity {

    private User otherUser;

    private Model m;
    private static RequestQueue queue;

    private Button followButton;
    private FloatingActionButton fab_followButton;
    private ListView postListView;

    private boolean is_self_profile;
    private boolean following;
    private boolean IS_TABLET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        m= Model.getInstance();
        queue= Volley.newRequestQueue(this);

        IS_TABLET= getResources().getBoolean(R.bool.isTablet);

        Intent intent= getIntent();
        String username= intent.getStringExtra("username");
        getSupportActionBar().setTitle(username);

        postListView= findViewById(R.id.others_posts);

        getUserInfo(username);

        is_self_profile = username.equals(m.getUsername());
        following= m.getActiveUserFriends().containsKey(username);
    }

    private void getUserInfo(final String username){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                Gson gson= new Gson();
                otherUser= gson.fromJson(response, User.class);

                updateUI();
                showPosts();
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(OthersProfile.this, "Impossibile ottenere informazioni utente", Toast.LENGTH_LONG).show();
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

    private void updateUI(){

        // img profilo
        ImageView profilePic= findViewById(R.id.img_profile_pic);
        Bitmap bitmap= ImageHandler.decodeString(otherUser.getImg());
        if(bitmap != null) {
            profilePic.setImageBitmap(bitmap);
        }

        // username
        TextView tv_username= findViewById(R.id.txt_username);
        tv_username.setText(otherUser.getUsername());

        // bottone segui
        if(IS_TABLET) {
            fab_followButton= findViewById(R.id.btn_segui);

            if (is_self_profile) {
                fab_followButton.setVisibility(View.GONE);
            } else if (following) {
                fab_followButton.setImageResource(R.drawable.person_added);
                fab_followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_light_blue)));
            } else {
                fab_followButton.setImageResource(R.drawable.person_add);
                fab_followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_gray)));
            }

            fab_followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followUnfollow();
                }
            });

            // infos
            TextView tv_infos= findViewById(R.id.txt_infos);
            String infos= String.valueOf(otherUser.getPosts().length)+" posts";
            tv_infos.setText(infos);
        }
        else{
            // bottone segui
            followButton = findViewById(R.id.btn_segui);
            if (is_self_profile) {
                followButton.setText(R.string.follow_yourself);
            } else if (following) {
                followButton.setBackgroundResource(R.drawable.button_light_blue_8dp_radius);
                followButton.setTextColor(getResources().getColor(R.color.white));
                followButton.setText(R.string.following);
            } else {
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

            // numero post
            TextView tv_posts= findViewById(R.id.txt_posts);
            tv_posts.setText(String.valueOf(otherUser.getPosts().length));
        }
    }

    private void followUnfollow() {
        if(IS_TABLET) {
            if (is_self_profile) {
                Toast.makeText(this, R.string.follow_yourself, Toast.LENGTH_SHORT).show();
            } else if (!following) {
                fab_followButton.setImageResource(R.drawable.person_added);
                fab_followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_light_blue)));

                followServerCall();
                Toast.makeText(this,
                        "Hai iniziato a seguire " + otherUser.getUsername(),
                        Toast.LENGTH_SHORT).show();

                following = !following;
            } else {
                fab_followButton.setImageResource(R.drawable.person_add);
                fab_followButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_gray)));

                unfollowServerCall();
                Toast.makeText(this,
                        "Non segui più " + otherUser.getUsername(),
                        Toast.LENGTH_SHORT).show();

                following = !following;
            }
        }
        else {
            if (is_self_profile) {
                Toast.makeText(this, R.string.follow_yourself, Toast.LENGTH_SHORT).show();
            } else if (! following) {
                // inizia a seguire
                followButton.setBackgroundResource(R.drawable.button_light_blue_8dp_radius);
                followButton.setTextColor(getResources().getColor(R.color.white));
                followButton.setText(R.string.following);

                followServerCall();
                Toast.makeText(this,
                        "Hai iniziato a seguire " + otherUser.getUsername(),
                        Toast.LENGTH_SHORT).show();

                following = !following;
            } else {
                // smetti di seguire
                followButton.setBackgroundResource(R.drawable.button_gray_outline);
                followButton.setTextColor(getResources().getColor(R.color.black));
                followButton.setText(R.string.not_following);

                unfollowServerCall();
                Toast.makeText(this,
                        "Non segui più " + otherUser.getUsername(),
                        Toast.LENGTH_SHORT).show();

                following = !following;
            }
        }
    }

    private void followServerCall(){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/follow";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.addFriend(otherUser.getUsername(), otherUser.getImg());
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
                params.put("username", otherUser.getUsername());

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
                m.removeFriend(otherUser.getUsername());
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
                params.put("username", otherUser.getUsername());

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private void showPosts(){
        if(otherUser.getPosts().length== 0) {
            TextView no_posts = findViewById(R.id.no_posts);

            if(is_self_profile) {
                no_posts.setVisibility(View.VISIBLE);
                no_posts.setText(R.string.you_have_no_posts);
            }else{
                no_posts.setVisibility(View.VISIBLE);
                no_posts.setText(R.string.they_have_no_posts);
            }
        }else{
            ProfilePostsAdapter adapter = new ProfilePostsAdapter(
                    this,
                    R.layout.item_user_posts_item,
                    otherUser.getPosts());
            postListView.setAdapter(adapter);
        }
    }

}
