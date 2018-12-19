package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.utente.fotogram.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Post> posts;
    private ImageHandler imageHandler;

    public PostsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public PostsAdapter(Context context, int resource, ArrayList<Post> posts) {
        super(context, resource, posts);
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
            v= li.inflate(R.layout.bacheca_post_list_item, null);
        }

        Post p= posts.get(position);

        if(p != null){
            ImageView image= v.findViewById(R.id.bacheca_post_item_picture);
            TextView didascalia= v.findViewById(R.id.bacheca_post_item_didascalia);

            Bitmap bitmap= imageHandler.decodeString(p.getImg());

            image.setImageBitmap(bitmap);
            didascalia.setText(p.getMsg());

        }

        return v;
    }
}
