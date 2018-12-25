package com.example.utente.fotogram;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.ServerService;

public class Login extends AppCompatActivity {

    private static Model m;
    private static ServerService serverService;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        m= Model.getInstance();
        serverService = ServerService.getInstance(Login.this);
//        oggetto richiamato nell'OnCreate di Navigation per
//        terminare Login e impedire di ritornarci (session_id consistency)
        activity= this;

//      commentare per invalidare le SharedPreferences
//        readPreferences();

        hideBottomNavBar();
        setConstraintLayoutListener();
        allowLandscape();

        final TextView tv_username = findViewById(R.id.txt_username);
        final TextView tv_password = findViewById(R.id.txt_password);
        final ProgressBar progressBar= findViewById(R.id.progressBar);

        Button login = findViewById(R.id.btn_accedi);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call login
                progressBar.setVisibility(View.VISIBLE);

                String username = tv_username.getText().toString();
                String password = tv_password.getText().toString();
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
//         il file viene scritto in Navigation
        SharedPreferences sharedPref= getSharedPreferences("preferences", Context.MODE_PRIVATE);

        String username= sharedPref.getString("username", null);
        String sessionID= sharedPref.getString("sessionID", null);

        if( sessionID != null && username != null){
            m.setActiveUserNickname(username);
            m.setSessionID(sessionID);
//      TODO: al Resume, l'app va brevemente alla Login e poi passa alla Navigation a causa del delay della chiamata di rete
            serverService.getActiveUserInfo(null, sessionID, username);
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
