package com.example.utente.fotogram;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;

public class OthersProfile extends AppCompatActivity {

    private User otherUser;
    private Model m;
    private ImageHandler imageHandler;
    private ServerService serverService;

    private Button followButton;

    private boolean is_self_profile;
    private boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        m= Model.getInstance();
        imageHandler= new ImageHandler(this);
        serverService= ServerService.getInstance(this);
        otherUser = m.getOtherUser();

        is_self_profile = otherUser.getUsername().equals(m.getActiveUserNickname());
        following= m.getActiveUserFriends().containsKey(otherUser.getUsername());

        getSupportActionBar().setTitle(otherUser.getUsername());

        // img profilo
        ImageView profilePic= findViewById(R.id.img_profile_pic);
        Bitmap bitmap= imageHandler.decodeString(otherUser.getImg());
        if(bitmap != null) {
            profilePic.setImageBitmap(bitmap);
        }

        // username
        TextView username= findViewById(R.id.txt_username);
        username.setText(otherUser.getUsername());

        // bottone segui
        followButton= findViewById(R.id.btn_segui);

        if(is_self_profile){
            followButton.setText(R.string.follow_yourself);
        }else if( following ){
            followButton.setText(R.string.following);
        }else{
            followButton.setText(R.string.not_following);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollow();
            }
        });
    }

    private void followUnfollow() {
        if( is_self_profile ){
            Toast.makeText(this, R.string.follow_yourself, Toast.LENGTH_SHORT).show();
        }else if(! following ) {
            followButton.setText(R.string.following);
            serverService.follow(m.getSessionID(), otherUser.getUsername(), otherUser.getImg());
            Toast.makeText(this,
                    "Hai iniziato a seguire " + otherUser.getUsername(),
                    Toast.LENGTH_SHORT).show();

            following= ! following;
        }else{
            followButton.setText(R.string.not_following);
            serverService.unfollow(m.getSessionID(), otherUser.getUsername());
            Toast.makeText(this,
                    "Non segui più "+ otherUser.getUsername(),
                    Toast.LENGTH_SHORT).show();

            following= ! following;
        }
    }


}
