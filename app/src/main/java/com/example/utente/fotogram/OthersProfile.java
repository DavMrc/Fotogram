package com.example.utente.fotogram;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.Object_classes.User;

import org.w3c.dom.Text;

public class OthersProfile extends AppCompatActivity {

    private User user;
    private Model m;
    private ImageHandler imageHandler;
    private ServerService serverService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        m= Model.getInstance();
        imageHandler= new ImageHandler(this);
        serverService= ServerService.getInstance(this);
        user= m.getOtherUser();

        getSupportActionBar().setTitle(user.getUsername());

        ImageView profilePic= findViewById(R.id.img_profile_pic);
        Bitmap bitmap =imageHandler.decodeString(user.getImg());
        if(bitmap != null) {
            profilePic.setImageBitmap(bitmap);
        }

        TextView username= findViewById(R.id.txt_username);
        username.setText(user.getUsername());

        Button followButton= findViewById(R.id.btn_segui);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollow();
            }
        });
    }

    private void followUnfollow(){

    }
}
