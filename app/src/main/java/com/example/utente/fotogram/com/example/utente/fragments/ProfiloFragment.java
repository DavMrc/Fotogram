package com.example.utente.fotogram.com.example.utente.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Model;
import com.example.utente.fotogram.R;

import java.io.ByteArrayOutputStream;

public class ProfiloFragment extends Fragment {

    private static Model m= Model.getInstance();

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profilo, container, false);

        TextView tv_username= v.findViewById(R.id.txt_username);
        tv_username.setText( m.getActiveUserNickname() );

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

//    public void encodeImage(int resource){
//        // la risorsa dev'essere raggiunta tramite drawable, altrimenti l'id punta al "container" della risorsa
//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resource);
//
//        new AsyncTask<Void, Void, String>(){
//            @Override
//            protected String doInBackground(Void... voids) {
//                ByteArrayOutputStream baos= new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte [] byteArr= baos.toByteArray();
//
//                String encoded= Base64.encodeToString(byteArr, Base64.DEFAULT);
//                return encoded;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                //                super.onPostExecute(s);
//                Toast.makeText(getContext(), "Finished", Toast.LENGTH_SHORT).show();
//                Log.d("DDD", "Encoded string: "+s);
//            }
//        }.execute();
//
//    }
}
