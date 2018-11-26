package com.example.utente.fotogram;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class Bacheca extends AppCompatActivity {

    final Fragment bacheca= new BachecaFragment();
    final Fragment cerca= new RicercaFragment();
    final Fragment nuovo_post= new Nuovo_Post_Fragment();
    final Fragment profilo= new ProfiloFragment();
    final FragmentManager fManager= getSupportFragmentManager();
    Fragment active= bacheca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacheca);

        hideBottomNavBar();
        setupFooter();

        // permette la versione landscape solo per i tablet
        if(getResources().getBoolean(R.bool.portait_only) == true){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onResume() {
        hideBottomNavBar();
        super.onResume();
    }

    private void hideBottomNavBar(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void setupFooter(){
        //aggiunge i fragment nascondendo quelli non utilizzati
        fManager.beginTransaction().add(R.id.main_container, bacheca, "1").commit();
        fManager.beginTransaction().add(R.id.main_container, cerca, "2").hide(cerca).commit();
        fManager.beginTransaction().add(R.id.main_container, nuovo_post, "3").hide(nuovo_post).commit();
        fManager.beginTransaction().add(R.id.main_container, profilo, "4").hide(profilo).commit();

//        assegna ad ogni elemento del nav footer un click event
        BottomNavigationView footer= findViewById(R.id.footer);
        footer.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bacheca:
                        fManager.beginTransaction().hide(active).show(bacheca).commit();
                        active= bacheca;
                        break;
                    case R.id.cerca:
                        fManager.beginTransaction().hide(active).show(cerca).commit();
                        active= cerca;
                        break;
                    case R.id.nuovo:
                        fManager.beginTransaction().hide(active).show(nuovo_post).commit();
                        active= nuovo_post;
                        break;
                    case R.id.profilo:
                        fManager.beginTransaction().hide(active).show(profilo).commit();
                        active= profilo;
                        break;
                }

                return true;
            }
        });
    }
}
