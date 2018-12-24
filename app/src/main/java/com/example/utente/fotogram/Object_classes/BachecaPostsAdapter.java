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
    private boolean following= false;

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
        //TODO: come fermo l'inflating se l'array è finito?

        Post p= posts[position];

        if(p != null){
            final String username= p.getUsername();
            final String img= friends.get(username);

            ImageView profileImg= v.findViewById(R.id.li_img_profilo);
            TextView tv_username= v.findViewById(R.id.li_username);
            ImageView unfollow= v.findViewById(R.id.li_unfollow);
            ImageView post= v.findViewById(R.id.li_post);
            TextView didascalia= v.findViewById(R.id.li_didascalia);

            Bitmap profileImgBitmap;
            Bitmap postBitmap= imageHandler.decodeString(p.getImg());

            if(! img.equals("")) {
                profileImgBitmap= imageHandler.decodeString(img);
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

            following= friends.containsKey(username);

            unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: se riclicca, dovrebbe ri-iniziare a seguire?

                    if(following){
                        // se l'utente cliccato non è sè stessi
                        if(! m.getActiveUserNickname().equals(username)) {
//                            serverService.unfollow(m.getSessionID(), username);
                            Toast.makeText(context, "Hai smesso di seguire " + username, Toast.LENGTH_SHORT).show();
                            following= ! following;
                        }else{
                            Toast.makeText(context,
                                    "Non puoi smettere di seguire te stesso",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        // se l'utente cliccato non è sè stessi
                        if(! m.getActiveUserNickname().equals(username)) {
//                            serverService.follow(m.getSessionID(), username, img);
                            Toast.makeText(context, "Hai iniziato a seguire " + username, Toast.LENGTH_SHORT).show();
                            following= ! following;
                        }else{
                            Toast.makeText(context,
                                    "Non iniziare a seguire te stesso",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }

        return v;
    }
}
