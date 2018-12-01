package com.example.utente.fotogram.Object_classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Login;
import com.example.utente.fotogram.Navigation;

import java.util.HashMap;
import java.util.Map;

public class ServerService {

    private static Model m;
    private Context context;

    public ServerService(Context context) {
        this.context= context;
        m= Model.getInstance();
    }

    public void login(final String username, final String password){

        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";
        final RequestQueue queue = Volley.newRequestQueue(context);

//      in un thread secondario, faccio il controllo delle credenziali
//      override di doinbackground contiene il ResponseListener delle due casistiche
//      di risposta: HTTP 200 o 400, gestite rispettivamente da onResponse e onErrorResponse

        new AsyncTask<Void, Void, StringRequest>() {

            @Override
            protected StringRequest doInBackground(Void... voids) {

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

                return request;
            }// chiude doInBackground

            @Override
            protected void onPostExecute(StringRequest stringRequest) {
                queue.add(stringRequest);
            }

        }.execute();
    }//chiude login

    public void logout(final String sessionID){
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/logout";
        final RequestQueue queue = Volley.newRequestQueue(context);

        new AsyncTask<Void, Void, StringRequest>() {

            @Override
            protected StringRequest doInBackground(Void... voids) {

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    // risposta valida
                    @Override
                    public void onResponse(String sessionID) {
//                      TODO: salvare eventuali dati salvabili (?)
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

                return request;
            }// chiude doInBackground

            @Override
            protected void onPostExecute(StringRequest stringRequest) {
                queue.add(stringRequest);
            }

        }.execute();
    }

    public void updatePicture(final String picture, final String sessionID){
        final RequestQueue queue= Volley.newRequestQueue(context);
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/picture_update";

        new AsyncTask<Void, Void, StringRequest>(){

            @Override
            protected StringRequest doInBackground(Void... voids) {

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

                return request;
            }

            @Override
            protected void onPostExecute(StringRequest stringRequest) {
                queue.add(stringRequest);
            }
        }.execute();
    }
}
