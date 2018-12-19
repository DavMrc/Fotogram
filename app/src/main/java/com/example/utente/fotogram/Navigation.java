package com.example.utente.fotogram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.com.example.utente.fragments.BachecaFragment;
import com.example.utente.fotogram.com.example.utente.fragments.Nuovo_Post_Fragment;
import com.example.utente.fotogram.com.example.utente.fragments.ProfiloFragment;
import com.example.utente.fotogram.com.example.utente.fragments.RicercaFragment;

public class Navigation extends AppCompatActivity {

    private Fragment bacheca= new BachecaFragment();
    private Fragment cerca= new RicercaFragment();
    private Fragment nuovo_post= new Nuovo_Post_Fragment();
    private Fragment profilo= new ProfiloFragment();
    private Fragment active= bacheca;
    private FragmentManager fManager= getSupportFragmentManager();

    private Toolbar toolbar;
    private ServerService serverService;

    private Model m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        m= Model.getInstance();
        serverService= ServerService.getInstance(this);

        hideBottomNavBar();

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: this doesn't get inflated right-away
        toolbar.inflateMenu(R.menu.refresh_button);
        getSupportActionBar().setTitle("Bacheca");

        setupFooter();

        // permette la versione landscape solo per i tablet
        if(getResources().getBoolean(R.bool.portait_only) == true){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveSession();
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideBottomNavBar();
    }

    private void hideBottomNavBar(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void saveSession(){
        String nickname= m.getActiveUserNickname();
        String sessionID= m.getSessionID();

        Context context= this;

        // crea le SharedPref di nome "preferences" e ci salva username e password
        // potranno essere usate da Login
        SharedPreferences sharedPref= context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPref.edit();

        editor.putString("username", nickname);
        editor.putString("sessionID", sessionID);

        editor.commit();
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

                        getSupportActionBar().setTitle("Bacheca");
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.refresh_button);
                        break;
                    case R.id.cerca:
                        fManager.beginTransaction().hide(active).show(cerca).commit();
                        active= cerca;

                        toolbar.getMenu().clear();
                        getSupportActionBar().setTitle("Cerca");
                        break;
                    case R.id.nuovo:
                        fManager.beginTransaction().hide(active).show(nuovo_post).commit();
                        active= nuovo_post;

                        toolbar.getMenu().clear();
                        getSupportActionBar().setTitle("Nuovo Post");
                        break;
                    case R.id.profilo:
                        fManager.beginTransaction().hide(active).show(profilo).commit();
                        active= profilo;

                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.logout_button);
                        getSupportActionBar().setTitle("Profilo");
                        break;
                }

                return true;
            }
        });
    }

    // gestisce tutti gli eventi delle Toolbar per ogni Fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_logout:
                serverService.logout();
                return true;
            case R.id.btn_refresh:
                // TODO: refresh bacheca
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
