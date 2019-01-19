package com.example.utente.fotogram;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.utente.fotogram.Model_Controller.Model;
import com.example.utente.fotogram.com.example.utente.fragments.BachecaFragment;
import com.example.utente.fotogram.com.example.utente.fragments.Nuovo_Post_Fragment;
import com.example.utente.fotogram.com.example.utente.fragments.ProfiloFragment;
import com.example.utente.fotogram.com.example.utente.fragments.RicercaFragment;

import java.util.Timer;
import java.util.TimerTask;

public class Navigation extends AppCompatActivity {

    private BachecaFragment bacheca= new BachecaFragment();
    private RicercaFragment cerca= new RicercaFragment();
    private Nuovo_Post_Fragment nuovo_post= new Nuovo_Post_Fragment();
    private ProfiloFragment profilo= new ProfiloFragment();
    private Fragment active= bacheca;
    private FragmentManager fManager= getSupportFragmentManager();

    private Toolbar toolbar;
    private View refreshView;

    private Model m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        m= Model.getInstance();
        Login.activity.finish();

        hideBottomNavBar();
        setupFooter();

        toolbar= findViewById(R.id.toolbar);
        toolbar.setTitle("Bacheca");
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        // permette la versione landscape solo per i tablet
//        if(getResources().getBoolean(R.bool.portait_only) == true){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
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

    private void saveSession(){
        String username= m.getUsername();
        String img= m.getImage(username);
        String sessionID= m.getSessionID();

        Context context= this;

        // crea le SharedPref di nome "preferences" e ci salva username e password
        // potranno essere usate da Login
        SharedPreferences sharedPref= context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPref.edit();

        editor.putString("username", username);
        editor.putString("img", img);
        editor.putString("sessionID", sessionID);

        editor.commit();
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
                        toolbar.inflateMenu(R.menu.refresh_button_bacheca);
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
                        toolbar.inflateMenu(R.menu.refresh_profilo_button);
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
                profilo.logout();
                return true;

            case R.id.btn_refresh_bacheca:
                // effettua l'animazione
                refreshView= findViewById(R.id.btn_refresh_bacheca);
                Animation animation= AnimationUtils.loadAnimation(this, R.anim.update_rotation);
                animation.setRepeatCount(Animation.INFINITE);
                refreshView.startAnimation(animation);
                item.setActionView(refreshView);

                bacheca.getWall();

                return true;

            case R.id.btn_refresh_profilo:
                // effettua l'animazione
                refreshView= findViewById(R.id.btn_refresh_profilo);
                Animation animation1= AnimationUtils.loadAnimation(this, R.anim.update_rotation);
                animation1.setRepeatCount(Animation.INFINITE);
                refreshView.startAnimation(animation1);
                item.setActionView(refreshView);

                profilo.getUserInfo();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // per gestire le autorizzazioni allo storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(active instanceof Nuovo_Post_Fragment){
                        nuovo_post.addImageFromGallery();
                    }else if(active instanceof ProfiloFragment){
                        profilo.changeProfilePic();
                    }
                }else {
                    Toast.makeText(this, "Permesso negato", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // chiamato dai Fragment
    public void stopRefreshAnimation(){
        if(refreshView != null) {
            refreshView.clearAnimation();
        }
    }

}
