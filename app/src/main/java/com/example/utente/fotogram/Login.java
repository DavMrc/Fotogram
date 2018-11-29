package com.example.utente.fotogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static Model m= Model.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkFile();

        hideBottomNavBar();
        setConstraintLayoutListener();
        allowLandscape();

        Button login = findViewById(R.id.btn_accedi);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onResume() {
        hideBottomNavBar();
        super.onResume();
    }

    private void checkFile(){
        try {
            FileInputStream inputStream= openFileInput("sessionIDfile");
            String s= IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
            Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void hideBottomNavBar(){
        // nasconde la bottom navbar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE         // immersive
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        );
    }

    private void setConstraintLayoutListener(){
        // aggiunge un listener a tutto il CL in modo da chiudere
        // la tastiera quando si fa click ovunque
        final ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
            }
        });
    }

    private void allowLandscape(){
        // permette la versione landscape solo per i tablet
        if( getResources().getBoolean(R.bool.portait_only) ){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void login(){

        TextView tv_username= findViewById(R.id.txt_nickname);
        TextView tv_password= findViewById(R.id.txt_password);

        final String username= tv_username.getText().toString();
        final String password= tv_password.getText().toString();
        final String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";

        final RequestQueue queue = Volley.newRequestQueue(this);

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
                        m.setActiveUser(username, sessionID);
                        startActivity(new Intent(Login.this, Navigation.class));
                    }
                }, new Response.ErrorListener() {
                    // risposta ad un errore
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Credenziali non valide", Toast.LENGTH_LONG).show();
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

}
