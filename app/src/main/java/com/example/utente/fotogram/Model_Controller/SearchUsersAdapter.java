package com.example.utente.fotogram.Model_Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utente.fotogram.R;

import java.util.ArrayList;

public class SearchUsersAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<User> users;

    public SearchUsersAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.context= context;
        this.users= users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v= convertView;

        if(v == null){
            //recycle if needed, else inflate
            LayoutInflater li;
            li= LayoutInflater.from(this.context);
            v= li.inflate(R.layout.item_search_result_item, null);
        }

        User u= users.get(position);

        if(u != null){
            ImageView image= v.findViewById(R.id.item_profile_pic);
            TextView username= v.findViewById(R.id.item_username);

            username.setText(u.getUsername());

            try{
                Bitmap bitmap = ImageHandler.decodeString(u.getImg());
                image.setImageBitmap(bitmap);
            }catch (Exception e) {
                // non gestisco l'errore perchè c'è il placeholder dell'immagine
            }
        }

        return v;
    }
}
