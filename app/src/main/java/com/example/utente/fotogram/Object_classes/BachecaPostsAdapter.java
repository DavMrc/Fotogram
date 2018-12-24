package com.example.utente.fotogram.Object_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.R;

import java.util.HashMap;

public class BachecaPostsAdapter extends ArrayAdapter {

    private Context context;
    private ServerService serverService;
    private ImageHandler imageHandler;
    private Model m;

    private Post [] posts;
    private HashMap<String, String> friends;

    public BachecaPostsAdapter(Context context, int resource, Post [] posts) {
        super(context, resource, posts);

        this.context = context;
        this.posts= posts;
        imageHandler= new ImageHandler(context);
        serverService= ServerService.getInstance(context);

        m= Model.getInstance();
        friends= m.getActiveUserFriends();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v= convertView;

        if(v == null){
            //recycle if needed, else inflate
            LayoutInflater li;
            li= LayoutInflater.from(this.context);
            v= li.inflate(R.layout.item_bacheca_post_item, null);
        }

        Post p= posts[position];

        if(p != null){
            final String username= p.getUsername();

            ImageView profileImg= v.findViewById(R.id.li_img_profilo);
            TextView tv_username= v.findViewById(R.id.li_username);
            ImageView unfollow= v.findViewById(R.id.li_unfollow);
            ImageView post= v.findViewById(R.id.li_post);
            TextView didascalia= v.findViewById(R.id.li_didascalia);

            Bitmap profileImgBitmap;
            Bitmap postBitmap= imageHandler.decodeString(p.getImg());

            if(! friends.get(username).equals("")) {
                profileImgBitmap= imageHandler.decodeString(friends.get(username));
                profileImg.setImageBitmap(profileImgBitmap);
            }

            tv_username.setText(username);

            post.setImageBitmap(postBitmap);

            if(p.getMsg().equals("")) {
                didascalia.setTypeface(null, Typeface.ITALIC);
                didascalia.setText(R.string.didascalia_empty);
            }else{
                didascalia.setTypeface(null, Typeface.NORMAL);
                didascalia.setText(p.getMsg());
            }

            unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // se l'utente cliccato non è sè stessi
                    if(! m.getActiveUserNickname().equals(username)) {
                        serverService.unfollow(m.getSessionID(), username);
                        Toast.makeText(context, "Hai smesso di seguire " + username, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,
                                "Non puoi smettere di seguire te stesso",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        return v;
    }
}
