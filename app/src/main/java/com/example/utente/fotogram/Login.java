package com.example.utente.fotogram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static Model m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        m= Model.getInstance();

//      TODO: per ora rimane commentato per aiutare il flow di navigazione
//        readPreferences();

        hideBottomNavBar();
        setConstraintLayoutListener();
        allowLandscape();

        Button login = findViewById(R.id.btn_accedi);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_username = findViewById(R.id.txt_nickname);
                TextView tv_password = findViewById(R.id.txt_password);
                String username = tv_username.getText().toString();
                String password = tv_password.getText().toString();

                ServerService serverService= new ServerService(Login.this);
                serverService.login(username, password);
            }
        });
    }

    @Override
    protected void onResume() {
        hideBottomNavBar();
        super.onResume();
    }

    private void readPreferences(){
        // legge il file con session ID, username ecc
        SharedPreferences sharedPref= getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String nickname= sharedPref.getString("username", null);
        String sessionID= sharedPref.getString("sessionID", null);

        if(sessionID != null && nickname != null){
            m.setActiveUserNickname(nickname);
            m.setSessionID(sessionID);

            startActivity(new Intent(Login.this, Navigation.class));
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
}
