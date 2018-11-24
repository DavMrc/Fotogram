package com.example.utente.fotogram;

import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hideBottomNavBar();

        Button login = findViewById(R.id.btn_accedi);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Bacheca.class));
            }
        });
    }

    @Override
    protected void onResume() {
        hideBottomNavBar();
        super.onResume();
    }

    private void hideBottomNavBar(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE         // immersive
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        );
    }

}
