package com.example.utente.fotogram.Object_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Navigation;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServerService {

    private static Model m;
    private Context context;
    private static RequestQueue queue;

    private static User user;
    private static String output;

    //costruttore
    public ServerService(Context context) {
        this.context= context;
        m= Model.getInstance();
    }

    public void login(final String username, final String password){

        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";
        queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                m.setActiveUserNickname(username);
                m.setSessionID(sessionID);
                context.startActivity(new Intent(context, Navigation.class));
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Credenziali non valide", Toast.LENGTH_LONG).show();
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
    }//chiude login

    public void logout(final String sessionID){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";
        queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
//             TODO: salvare eventuali dati salvabili (?)
                m.setSessionID(null);
                context.startActivity(new Intent(context, Login.class));
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Impossibile fare logout", Toast.LENGTH_LONG).show();
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
        queue= Volley.newRequestQueue(context);
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/picture_update";

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
                params.put("session_id", sessionID);
                params.put("picture", picture);

                return params;
            }
        };

        queue.add(request);

    }

    public String getUser(){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/profile";
        final String sessionID= m.getSessionID();
        final String username= m.getActiveUserNickname();

        queue= Volley.newRequestQueue(context);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            // risposta valida
            @Override
            public void onResponse(JSONObject jsonObject) {

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
                params.put("username", username);

                return params;
            }
        };// finisce la Request

        queue.add(jsonRequest);

        return output;
    }

    private void parseJsonUser(String jsonObject){
        Gson gson= new Gson();
        user= gson.fromJson(jsonObject, User.class);
    }
}
