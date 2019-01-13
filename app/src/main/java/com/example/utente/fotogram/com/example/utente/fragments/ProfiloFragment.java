package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Model_Controller.ImageHandler;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.Post;
import com.example.utente.fotogram.Model_Controller.ProfilePostsAdapter;
import com.example.utente.fotogram.Model_Controller.User;
import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfiloFragment extends Fragment {

    private Model m;
    private View view;
    private User user;

    public ImageView proPic;
    private Context context;
    private static RequestQueue queue;
    private ImageHandler imageHandler;

    private int friendsCount;
    private TextView tv_friends;
    private TextView tv_username;
    private Button changeProPic;
    private ListView postsListView;

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        friendsCount= m.getActiveUserFriends().size()-1;
        tv_friends.setText(String.valueOf(friendsCount));

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profilo, container, false);

        context= getContext();
        m= Model.getInstance();
        friendsCount= m.getActiveUserFriends().size()-1; // -1 perchè include l'activeUser
        queue= Volley.newRequestQueue(context);
        imageHandler= new ImageHandler(context);

        proPic= view.findViewById(R.id.img_profile_pic);
        postsListView= view.findViewById(R.id.personal_posts);
        tv_username= view.findViewById(R.id.txt_username);
        tv_friends = view.findViewById(R.id.txt_seguiti);
        changeProPic = view.findViewById(R.id.btn_change_profile_pic);

        getUserInfo();

        return view;
    }

    public void getUserInfo(){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                Gson gson= new Gson();
                user= gson.fromJson(response, User.class);

                updateUI();
                showPosts();
                ((Navigation)getActivity()).stopRefreshAnimation();
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
                params.put("username", m.getUsername());

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    private void updateUI(){
//        immagine profilo
        if(user.getImg() != null){
            proPic.setImageBitmap(imageHandler.decodeString(user.getImg()));
        }// altrimenti c'è il placeholder

//        username
        tv_username.setText(user.getUsername());

//        seguiti/amici che si sta seguendo
        tv_friends.setText(String.valueOf(friendsCount));

//        bottone cambia immagine
        changeProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
            }
        });
    }

    private void checkStoragePermissions(){
//        permission isn't granted: prompt the user
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
//            permissions were already granted, proceed normally
            changeProfilePic();
        }
    }

    public void changeProfilePic(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturePath= pictureDirectory.getPath();

        Uri actualPictures= Uri.parse(picturePath);
        photoPickerIntent.setDataAndType(actualPictures,"image/*");
        startActivityForResult(photoPickerIntent, 20);
    }

    @Override
    // relativo al photoPickerIntent, serve per aggiunge l'immagine
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 20 & resultCode == Activity.RESULT_OK) {
            Uri imageURI = data.getData();

            if(imageURI != null){
                proPic.setImageURI(imageURI);

//              aggiorna su server e Model
                String encoded= imageHandler.encodeFromUri(imageURI);
                updatePictureOnServer(encoded);

//                m.setImage(encoded);
            }
        }
    }

    private void updatePictureOnServer(final String encoded){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/picture_update";

        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Aggiornata immagine su server", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Richiesta al server errata", Toast.LENGTH_SHORT).show();
            }
        }){
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("picture", encoded);

                return params;
            }
        };

        queue.add(request);
    }

    public void showPosts(){
        ProfilePostsAdapter adapter = new ProfilePostsAdapter(
                context,
                R.layout.item_user_posts_item,
                user.getPosts());
        postsListView.setAdapter(adapter);
    }

    public void logout(){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
//                CANCELLA le sharedPreferences
                try {
                    SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.commit();
                }catch (Exception e){ }

                m.setSessionID(null);
                context.startActivity(new Intent(context, Login.class));
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Impossibile fare logout", Toast.LENGTH_LONG).show();
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

}
