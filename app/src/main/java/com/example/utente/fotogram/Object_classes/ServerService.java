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

public class ServerService extends Activity {

    private static final Model m= Model.getInstance();
    private Context context;

    public ServerService(Context context) {
        this.context= context;
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
    }
}
