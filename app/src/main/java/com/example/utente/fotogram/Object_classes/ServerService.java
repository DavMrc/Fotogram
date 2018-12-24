package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.OthersProfile;
import com.example.utente.fotogram.com.example.utente.fragments.BachecaFragment;
import com.example.utente.fotogram.com.example.utente.fragments.ProfiloFragment;
import com.example.utente.fotogram.com.example.utente.fragments.RicercaFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerService {

    private static ServerService serverService;
    private static Model m;
    private static Context privateContext;
    private static RequestQueue queue;

    private static boolean first_login= true;
    private static boolean first_getUserInfo= true;
    private static boolean first_getFriends= true;

//    per evitare concatenazioni di metodi (Login -> getUserInfo -> getFriends)
//    la prima volta che ci si loggava, sono stati inseriti tre boolean che,
//    nei rispettivi metodi, controllano se si sta eseguendo il login la prima volta
//    allora lascia che si concatenino, per le successive i boolean impediranno
//    la concatenazione

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
        /* dopo aver effettuato
        la chiamata di rete /login, setta nel model sessionID e username, per poi
        chiamare il metodo getActiveUserInfo, che ottiene l'immagine e la lista dei post.
        Questo è stato fatto per motivi di sincronizzazione.
        */
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.setSessionID(sessionID);

                Log.d("DDD", "DDD ServerService Session id: "+sessionID);

                if(first_login) {
                    first_login= false;
                    getActiveUserInfo(null, sessionID, username);
                }
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

    public void updatePicture(final String sessionID, final String picture){
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

    public void getActiveUserInfo(final Fragment callingFragment, final String sessionID, final String username){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                User activeUser= parseUser(response);

                m.setActiveUser(activeUser);

//                per evitare concatenazioni di metodi
                if(first_getUserInfo) {
                    first_getUserInfo= false;
                    getFriends(null, sessionID, "active");
                }else {
//                esegui due operazioni diverse in base al Fragment chiamante
                    if (callingFragment instanceof BachecaFragment) {
                        BachecaFragment fr = (BachecaFragment) callingFragment;
                        fr.onRefreshUserInfo();
                    } else if (callingFragment instanceof ProfiloFragment) {
                        ProfiloFragment fr = (ProfiloFragment) callingFragment;
                        fr.onRefreshUserInfo();
                    }
                }
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

    public void createPost(final String sessionID, final Post post){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/create_post";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                Toast.makeText(privateContext, "Immagine inviata al server correttamente", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Impossibile inviare immagine al server", Toast.LENGTH_LONG).show();
//                Log.d("DDD", "DDD CreatePost Session id: "+sessionID);
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", sessionID);
                params.put("img", post.getImg());
                params.put("message", post.getMsg());

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    public void searchUser(final RicercaFragment fragment, final String usernamestart){

        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/users";
        final String session_id= m.getSessionID();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String serverResponse) {
                ArrayList<User> users = parseSearchUsers(serverResponse);
                fragment.onPostServerRequest(users);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(privateContext, "Impossibile effettuare ricerca", Toast.LENGTH_LONG).show();
            }
        }) {
            // parametri richiesta POST
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", session_id);
                params.put("usernamestart", usernamestart);

                return params;
            }
        };// finisce la StringRequest

        queue.add(request);
    }

    public void getOtherUserInfo(final String sessionID, final String username){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String response) {
                User otherUser= parseUser(response);
                m.setOtherUser(otherUser);

                privateContext.startActivity(new Intent(privateContext, OthersProfile.class));
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

    public void getFriends(Fragment callingFragment, final String sessionID, final String who){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/followed";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String serverResponse) {
                HashMap<String, String> friends = parseFriends(serverResponse);

                m.setActiveUserFriends(friends);

                if (first_getFriends) {
                    first_getFriends = false;
//                finally, move on to next Activity
                    privateContext.startActivity(new Intent(privateContext, Navigation.class));
                }
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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

    public void follow(final String sessionID, final String username, final String img){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/follow";

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
                Toast.makeText(privateContext, "Impossibile seguire", Toast.LENGTH_LONG).show();
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

    public void unfollow(final String sessionID, final String username){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/unfollow";

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
                Toast.makeText(privateContext, "Impossibile smettere di seguire", Toast.LENGTH_LONG).show();
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

    //JSON handlers
    private User parseUser(String jsonObject){
        Gson gson= new Gson();

        return gson.fromJson(jsonObject, User.class);
    }

    private HashMap<String, String> parseFriends(String serverResponse){
        HashMap<String, String> friends= new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONArray array= jsonObject.getJSONArray("followed");

            for(int i=0; i < array.length(); i++){
                JSONObject pointedUser= array.getJSONObject(i);
                String username= pointedUser.getString("name");
                String picture= pointedUser.getString("picture");

                friends.put(username, picture);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return friends;
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
