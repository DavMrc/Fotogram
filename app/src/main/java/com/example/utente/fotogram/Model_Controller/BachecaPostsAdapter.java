package com.example.utente.fotogram.Model_Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.OthersProfile;
import com.example.utente.fotogram.R;

import java.util.HashMap;
import java.util.Map;

public class BachecaPostsAdapter extends ArrayAdapter {

    private Context context;
    private Model m;
    private static RequestQueue queue;

    private Post [] posts;
    private HashMap<String, String> friends;
    private boolean following= false;

    private Typeface roboto_light;
    private Typeface roboto_light_italic;

    public BachecaPostsAdapter(Context context, int resource, Post [] posts) {
        super(context, resource, posts);

        this.context = context;
        this.posts= posts;

        m= Model.getInstance();
        friends= m.getActiveUserFriends();
        queue= Volley.newRequestQueue(context);

        roboto_light= ResourcesCompat.getFont(context, R.font.roboto_light);
        roboto_light_italic= ResourcesCompat.getFont(context, R.font.roboto_light_italic);

        // roboto_light= Typeface.createFromAsset(context.getAssets(),  "fonts/roboto_light.ttf");
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v= convertView;

        if(v == null){
            //recycle if needed, else inflate
            LayoutInflater li;
            li= LayoutInflater.from(this.context);
            v= li.inflate(R.layout.item_bacheca_post_item, null);
        }

        final Post p= posts[position];

        ImageView profileImg= v.findViewById(R.id.li_img_profilo);
        TextView tv_username= v.findViewById(R.id.li_username);
        ImageView unfollow= v.findViewById(R.id.li_unfollow);
        ImageView post= v.findViewById(R.id.li_post);
        TextView didascalia= v.findViewById(R.id.li_didascalia);

        if(p != null){
            final String username= p.getUsername();
            final String img= m.getImage(username);

            Bitmap profileImgBitmap;
            Bitmap postBitmap= ImageHandler.decodeString(p.getImg());

            try {
                if (!img.equals("")) {
                    profileImgBitmap = ImageHandler.decodeString(img);
                    profileImg.setImageBitmap(profileImgBitmap);
                }
            }catch (Exception e){}

            tv_username.setText(username);

            post.setImageBitmap(postBitmap);

            if(p.getMsg().equals("")) {
                didascalia.setTypeface(roboto_light_italic);
                didascalia.setText(R.string.didascalia_empty);
            }else{
                didascalia.setTypeface(roboto_light);
                didascalia.setText(p.getMsg());
            }

            following= friends.containsKey(username);

            unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(following){
                        // se l'utente cliccato non è sè stessi
                        if(! m.getUsername().equals(username)) {
                            unfollowServerCall(username);
                            Toast.makeText(context,
                                    "Hai smesso di seguire " + username,
                                    Toast.LENGTH_SHORT).show();
                            following= ! following;
                        }else{
                            Toast.makeText(context,
                                    "Non puoi smettere di seguire te stesso",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, OthersProfile.class);
                    intent.putExtra("username", p.getUsername());

                    if(! isConnected()) {
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    }else {
                        context.startActivity(intent);
                    }
                }
            });

        }

        return v;
    }

    private void unfollowServerCall(final String username){
        if(! isConnected()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }else {
            String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/unfollow";

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
                    Toast.makeText(context, "Impossibile smettere di seguire", Toast.LENGTH_LONG).show();
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
    }

    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
