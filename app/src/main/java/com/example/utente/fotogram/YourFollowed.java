package com.example.utente.fotogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.utente.fotogram.Model_Controller.Model;

public class YourFollowed extends AppCompatActivity {

    private Model m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_followed);

        m= Model.getInstance();
    }
}
