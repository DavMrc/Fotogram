package com.example.utente.fotogram;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ProfiloFragment extends Fragment {

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_profilo, container, false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // la risorsa dev'essere raggiunta tramite drawable, altrimenti l'id punta al "container" della risorsa
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zeus);

        Button btn= getView().findViewById(R.id.babb);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, String>(){
                    @Override
                    protected String doInBackground(Void... voids) {
                        ByteArrayOutputStream baos= new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte [] byteArr= baos.toByteArray();

                        String encoded= Base64.encodeToString(byteArr, Base64.DEFAULT);
                        return encoded;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        //                super.onPostExecute(s);
                        Toast.makeText(getContext(), "Finished", Toast.LENGTH_SHORT).show();
                        Log.d("DDD", "Encoded string: "+s);
                    }
                }.execute();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
