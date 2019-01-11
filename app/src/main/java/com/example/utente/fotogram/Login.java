package com.example.utente.fotogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.Model_Controller.ServerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private static Model m;
    public static Activity activity;
    private static RequestQueue queue;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        m= Model.getInstance();
        queue= Volley.newRequestQueue(Login.this);
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
        progressBar= findViewById(R.id.progressBar);

        Button login = findViewById(R.id.btn_accedi);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call login

                progressBar.setVisibility(View.VISIBLE);

                String username = tv_username.getText().toString();
                String password = tv_password.getText().toString();

                login(username, password);
            }
        });
    }

    private void login(final String username, final String password){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/login";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String sessionID) {
                Log.d("DDD", "DDD ServerService Session id: "+sessionID);

                m.setSessionID(sessionID);
                m.setUsername(username);

                getFriends(sessionID);
            }
        }, new Response.ErrorListener() {
            // risposta ad un errore
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Login.this, "Credenziali non valide", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
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

    private void getFriends(final String sessionID){
        String url= "https://ewserver.di.unimi.it/mobicomp/fotogram/followed";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            // risposta valida
            @Override
            public void onResponse(String serverResponse) {
                m.setActiveUserFriends( parseFriends(serverResponse));

                startActivity(new Intent(Login.this, Navigation.class));
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

    @Override
    protected void onResume() {
        hideBottomNavBar();
        super.onResume();
    }

    private void readPreferences(){
        // legge il file con session ID, username ecc
//         il file viene scritto in Navigation
//        SharedPreferences sharedPref= getSharedPreferences("preferences", Context.MODE_PRIVATE);
////
////        String username= sharedPref.getString("username", null);
////        String img= sharedPref.getString("img", null);
////        String sessionID= sharedPref.getString("sessionID", null);
////
////        if( username != null && img != null && sessionID != null ){
////            //TODO: tutto
//////      TODO: al Resume, l'app va brevemente alla Login e poi passa alla Navigation a causa del delay della chiamata di rete
////            serverService.getActiveUserInfo(null, sessionID, username);
////        }

//        String fileName= "saved_user";
//
//        try {
//            FileInputStream fis = openFileInput(fileName);
//            ObjectInputStream ois= new ObjectInputStream(fis);
//
//            User restoredUser= (User) ois.readObject();
////            m.setActiveUser(restoredUser);
//
//            fis.close();
//            ois.close();
//
//            Toast.makeText(this,
//                    "Success in reading",
//                    Toast.LENGTH_SHORT).show();
//        }catch (Exception e){
//            Toast.makeText(this,
//                    "Errore durante la lettura dell'utente",
//                    Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }

    }

    private void hideBottomNavBar(){
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
//                barra di sistema riapparsa, nasconderla
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
                    decorView.setSystemUiVisibility(
                              View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    );
                }
            }
        });
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

//    -----------------JSON HANDLER---------------------

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
}
