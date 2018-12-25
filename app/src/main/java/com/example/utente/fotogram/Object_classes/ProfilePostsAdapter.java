package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utente.fotogram.R;

import java.util.ArrayList;

public class ProfilePostsAdapter extends ArrayAdapter {

    private Context context;
    private ImageHandler imageHandler;
    private Post [] posts;

    public ProfilePostsAdapter(Context context, int resource, Post [] posts) {
        super(context, resource, posts);
        this.context = context;
        imageHandler= new ImageHandler(context);
        this.posts= posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v= convertView;

        if(v == null){
            //recycle if needed, else inflate
            LayoutInflater li;
            li= LayoutInflater.from(this.context);
            v= li.inflate(R.layout.item_user_posts_item, null);
        }

        Post p= posts[position];

        if(p != null){
            ImageView image= v.findViewById(R.id.item_post_item_img);
            TextView didascalia= v.findViewById(R.id.item_post_item_msg);

            Bitmap bitmap= imageHandler.decodeString(p.getImg());

            if(bitmap != null) {
                image.setImageBitmap(bitmap);
            }
            if(p.getMsg().equals("")) {
                didascalia.setTypeface(null, Typeface.ITALIC);
                didascalia.setText(R.string.didascalia_empty);
            }else{
                didascalia.setTypeface(null, Typeface.NORMAL);
                didascalia.setText(p.getMsg());
            }

        }

        return v;
    }
}
