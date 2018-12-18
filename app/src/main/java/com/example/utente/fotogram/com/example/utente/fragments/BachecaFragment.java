package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.utente.fotogram.R;

public class BachecaFragment extends Fragment {

    public BachecaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_bacheca, container, false);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: this doesn't work. check ProfiloFragment if already solved
        switch (item.getItemId()){
            case R.id.btn_refresh:
                //refresh bacheca
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
