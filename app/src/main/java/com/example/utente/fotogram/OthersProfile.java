package com.example.utente.fotogram;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OthersProfile extends AppCompatActivity {

    private User user;
    private Model m;
    private ImageHandler imageHandler;
    private ServerService serverService;

    private Button followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        m= Model.getInstance();
        imageHandler= new ImageHandler(this);
        serverService= ServerService.getInstance(this);
        user= m.getOtherUser();

        getSupportActionBar().setTitle(user.getUsername());

        // img profilo
        ImageView profilePic= findViewById(R.id.img_profile_pic);
        Bitmap bitmap= imageHandler.decodeString(user.getImg());
        if(bitmap != null) {
            profilePic.setImageBitmap(bitmap);
        }

        // username
        TextView username= findViewById(R.id.txt_username);
        username.setText(user.getUsername());

        // bottone segui
        followButton= findViewById(R.id.btn_segui);

        // se l'utente è gia nella lista degli amici
        if( m.getActiveUserFriends().containsKey(user.getUsername()) ){
            followButton.setText(R.string.following);
        } // se il profilo selezionato è sè stessi
        if( user.getUsername().equals(m.getActiveUserNickname()) ){
            followButton.setText(R.string.follow_yourself);
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
        // se non si sta già seguendo l'utente
        if (! m.getActiveUserFriends().containsKey(user.getUsername())) {
            followButton.setText(R.string.following);
            serverService.follow(m.getSessionID(), user.getUsername(), user.getImg());
            Toast.makeText(this, "Hai iniziato a seguire " + user.getUsername(), Toast.LENGTH_SHORT).show();
        } // se l'utente è sè stessi
        if( m.getActiveUserFriends().containsKey(user.getUsername()) ){
            Toast.makeText(this, R.string.follow_yourself, Toast.LENGTH_SHORT).show();
        }else{
            followButton.setText(R.string.not_following);
            serverService.unfollow(m.getSessionID(), user.getUsername());
            Toast.makeText(this, "Non segui più "+user.getUsername(), Toast.LENGTH_SHORT).show();
        }
    }
}
