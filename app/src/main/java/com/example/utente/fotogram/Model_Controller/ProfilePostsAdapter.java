package com.example.utente.fotogram.Model_Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utente.fotogram.R;

import java.lang.reflect.Type;

public class ProfilePostsAdapter extends ArrayAdapter {

    private Context context;
    private ImageHandler imageHandler;
    private Post [] posts;

    private Typeface roboto_light;
    private Typeface roboto_light_italic;

    public ProfilePostsAdapter(Context context, int resource, Post [] posts) {
        super(context, resource, posts);
        this.context = context;
        this.posts= posts;
        imageHandler= new ImageHandler(context);

        roboto_light= ResourcesCompat.getFont(context, R.font.roboto_light);
        roboto_light_italic= ResourcesCompat.getFont(context, R.font.roboto_light_italic);
        // roboto_light_italic= Typeface.createFromAsset(context.getAssets(),  "fonts/roboto_light_italic.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v= convertView;

        if (v == null) {
            //recycle if needed, else inflate
            LayoutInflater li;
            li = LayoutInflater.from(this.context);
            v = li.inflate(R.layout.item_user_posts_item, null);
        }

        Post p = posts[position];

        if (p != null) {
            ImageView image = v.findViewById(R.id.item_post_item_img);
            TextView didascalia = v.findViewById(R.id.item_post_item_msg);

            Bitmap bitmap = imageHandler.decodeString(p.getImg());

            if (bitmap != null) {
                image.setImageBitmap(bitmap);
            }
            if (p.getMsg().equals("")) {
                didascalia.setTypeface(roboto_light_italic);
                didascalia.setText(R.string.didascalia_empty);
            } else {
                didascalia.setTypeface(roboto_light);
                didascalia.setText(p.getMsg());
            }

        }

        return v;
    }
}
