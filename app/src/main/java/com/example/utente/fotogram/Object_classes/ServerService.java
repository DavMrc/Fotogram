package com.example.utente.fotogram.Object_classes;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Navigation;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerService {

    private static ServerService serverService;
    private static Model m;
    private static Context privateContext;
    private static RequestQueue queue;

    private static User user;
    private ArrayList<User> users;  //per la chiamata searchUser

    //costruttore singleton
    private ServerService() {}

    public static synchronized ServerService getInstance(Context context){
        if(serverService == null){
            serverService= new ServerService();
            queue = Volley.newRequestQueue(context);
            queue.start();
        }

        privateContext = context;
        m= Model.getInstance();

        return serverService;
    }

    public void login(final String username, final String password){
        /* chiamato in Login.class, ovvero la prima activity. dopo aver effettuato
        la chiamata di rete /login, setta nel model sessionID e username, per poi
        chiamare il metodo getUserInfo, che ottiene l'immagine e la lista dei post.
        Questo è stato fatto per motivi di sincronizzazione.
        */
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.setSessionID(sessionID);
                m.setActiveUserNickname(username);

                getUserInfo(sessionID, username);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Credenziali non valide", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    public void logout(final String sessionID){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
//                CANCELLA le sharedPreferences
                SharedPreferences sharedPref= privateContext.getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPref.edit();
                editor.clear();
                editor.commit();

                m.setSessionID(null);
                privateContext.startActivity(new Intent(privateContext, Login.class));
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Impossibile fare logout", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", sessionID);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);

    }

    public void updatePicture(final String picture, final String sessionID){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/picture_update";

        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(privateContext, "Aggiornata immagine su server", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(privateContext, "Richiesta al server errata", Toast.LENGTH_SHORT).show();
            }
        }){
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", sessionID);
                params.put("picture", picture);

                return params;
            }
        };

        queue.add(request);

    }

    public void getUserInfo(final String sessionID, final String username){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                parseActiveUser(response);

                privateContext.startActivity(new Intent(privateContext, Navigation.class));
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Impossibile ottenere informazioni utente", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", sessionID);
                params.put("username", username);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    public void createPost(final Post post){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/create_post";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                Toast.makeText(privateContext, "Immagine inviata al server correttamente", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Impossibile inviare immagine al server", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", m.getSessionID());
                params.put("img", post.getPicture());
                params.put("message", post.getMsg());

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    public ArrayList<User> searchUser(final String usernamestart){

        users= new ArrayList<>();

        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/users";
        final String session_id= m.getSessionID();

//        ASINCRONA

//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            // risposta valida
//            @Override
//            public void onResponse(String serverResponse) {
//                parseUsers(serverResponse);
//            }
//        }, new Response.ErrorListener() {
//            // risposta ad un errore
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Toast.makeText(privateContext, "Impossibile effettuare ricerca", Toast.LENGTH_LONG).show();
//            }
//        }) {
//            // parametri richiesta POST
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("session_id", session_id);
//                params.put("usernamestart", usernamestart);
//
//                return params;
//            }
//        };// finisce la StringRequest
//
//        queue.add(request);

//        SINCRONA

//        RequestFuture<String> future= RequestFuture.newFuture();
//        StringRequest stringRequest= new StringRequest(Request.Method.POST, url, future, future) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("session_id", session_id);
//                params.put("usernamestart", usernamestart);
//
//                return params;
//            }
//        };
//
//        queue.add(stringRequest);
//
//        try{
//            String response= future.get(4, TimeUnit.SECONDS);
//            parseUsers(response);
//        }catch (Exception e){
//            Toast.makeText(privateContext, "Porco dio", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }

        return users;
    }

    //JSON handlers
    private void parseActiveUser(String jsonObject){
        Gson gson= new Gson();
        user= gson.fromJson(jsonObject, User.class);

        String img= user.getImg();
        m.setActiveUserImg(img);
    }

    private void parseUsers(String jsonResponse){

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
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
    }
}
