package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

public class ProfiloFragment extends Fragment {

    private Model m;
    private View view;
    private User user;

    public ImageView proPic;
    private Context context;
    private static RequestQueue queue;

    private int friendsCount;
    private TextView tv_friends;
    private TextView tv_username;
    private TextView tv_no_posts;
    private ImageButton changeProPic;
    private ListView postsListView;

    private int PROFILE_IMAGE_SIZE= 10;

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        if( m.getActiveUserFriends() != null) {
            friendsCount = m.getActiveUserFriends().size() - 1;
            tv_friends.setText(String.valueOf(friendsCount));
        }

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profilo, container, false);

        context= getContext();
        m= Model.getInstance();
        queue= Volley.newRequestQueue(context);

        proPic= view.findViewById(R.id.img_profile_pic);
        postsListView= view.findViewById(R.id.personal_posts);
        tv_username= view.findViewById(R.id.txt_username);
        tv_friends = view.findViewById(R.id.txt_seguiti);
        tv_no_posts= view.findViewById(R.id.no_posts);
        changeProPic = view.findViewById(R.id.btn_change_profile_pic);

        getUserInfo();

        return view;
    }

    public void getUserInfo(){
        if(! isConnected()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            ((Navigation) getActivity()).stopRefreshAnimation();
        }else {
            String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                // risposta valida
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    user = gson.fromJson(response, User.class);

                    updateUI();
                    showPosts();
                    ((Navigation) getActivity()).stopRefreshAnimation();
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
    }

    private void updateUI(){
//        immagine profilo
        if(m.getImage(m.getUsername()) != null){
            proPic.setImageBitmap(ImageHandler.decodeString(m.getImage(m.getUsername())));
        }else if(user.getImg() != null){
            proPic.setImageBitmap(ImageHandler.decodeString(user.getImg()));
        }// altrimenti c'è il placeholder

//        username
        tv_username.setText(user.getUsername());

//        seguiti/amici che si sta seguendo
        friendsCount= m.getActiveUserFriends().size()-1; // -1 perchè include l'activeUser
        String frCnt= String.valueOf(friendsCount)+" seguiti";
        tv_friends.setText(frCnt);

//        bottone cambia immagine
        changeProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
            }
        });

//        se non hai postato nulla
        if(user.getPosts().length == 0) {
            tv_no_posts.setVisibility(View.VISIBLE);
            tv_no_posts.setText(R.string.you_have_no_posts);
        }
    }

    public void showPosts(){
        ProfilePostsAdapter adapter = new ProfilePostsAdapter(
                context,
                R.layout.item_user_posts_item,
                user.getPosts());
        postsListView.setAdapter(adapter);
    }

    private void checkStoragePermissions(){
//        permission isn't granted: prompt the user
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
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

//              aggiorna su server e model
                encodeFromUri(imageURI);
            }
        }
    }

    public void encodeFromUri(Uri uri){
        String [] filePathColumn= {MediaStore.Images.Media.DATA};

        Cursor cursor= context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex= cursor.getColumnIndex(filePathColumn[0]);
        String path= cursor.getString(columnIndex);
        cursor.close();

        File imageAsFile= new File(path);
        final String[] mString = new String[1];

        if(imageAsFile.length()/1024 > PROFILE_IMAGE_SIZE){
            Luban.compress(context, imageAsFile).setMaxSize(PROFILE_IMAGE_SIZE).putGear(Luban.CUSTOM_GEAR).launch(new OnCompressListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(File file) {
                    mString[0] = ImageHandler.fileToBase64(file);
                    updatePictureOnServer(mString[0]);
                    m.setImage(mString[0]);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(context,
                            "Errore durante la compressione dell'immagine",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            mString[0] = ImageHandler.fileToBase64(imageAsFile);
            updatePictureOnServer(mString[0]);
            m.setImage(mString[0]);
        }
    }

    private void updatePictureOnServer(final String encoded){
        if(! isConnected()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }else {
            String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/picture_update";

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, "Aggiornata immagine su server", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Impossibile aggiornare immagine su server", Toast.LENGTH_SHORT).show();
                }
            }) {
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
    }

    public void logout(){
        if(! isConnected()) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }else {
            String url = "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";

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
                    } catch (Exception e) {
                    }

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
