package com.example.utente.fotogram.Object_classes;

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

public class PostsAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Post> posts;
    private ImageHandler imageHandler;

    public PostsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public PostsAdapter(Context context, int resource, ArrayList<Post> posts) {
        super(context, resource, posts);
        this.context = context;
        imageHandler= new ImageHandler(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: check errors
        View v= convertView;

        if(v == null){
            //recycle if needed, else inflate
            LayoutInflater li;
            li= LayoutInflater.from(this.context);
            v= li.inflate(R.layout.item_logged_user_posts_item, null);
        }

        Post p= posts.get(position);

        if(p != null){
            ImageView image= v.findViewById(R.id.bacheca_post_item_picture);
            TextView didascalia= v.findViewById(R.id.bacheca_post_item_didascalia);

            Bitmap bitmap= imageHandler.decodeString(p.getImg());

            if(bitmap != null) {
                image.setImageBitmap(bitmap);
            }
            didascalia.setText(p.getMsg());

        }

        return v;
    }
}
