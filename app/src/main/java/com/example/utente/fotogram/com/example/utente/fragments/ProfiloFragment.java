package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Navigation;
import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.PostsAdapter;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.R;

import java.io.File;

public class ProfiloFragment extends Fragment {

    private Model m;

    public ImageView proPic;
    private Context context;
    private ServerService serverService;
    private ImageHandler imageHandler;

    private int friendsCount;
    private TextView tv_friends;
    private ListView postsListView;

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        friendsCount= m.getActiveUserFriends().size()-1;
        tv_friends.setText(String.valueOf(friendsCount));

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profilo, container, false);

        context= getContext();
        m= Model.getInstance();
        friendsCount= m.getActiveUserFriends().size()-1; // -1 perchè include l'activeUser
        serverService= ServerService.getInstance(context);
        imageHandler= new ImageHandler(context);

        proPic= v.findViewById(R.id.img_profile_pic);
        postsListView= v.findViewById(R.id.personal_posts);

//        immagine profilo
        String image= m.getActiveUserImg();
        if(image != null){
            proPic.setImageBitmap(imageHandler.decodeString(image));
        }// altrimenti c'è il placeholder

//        username
        TextView username= v.findViewById(R.id.txt_username);
        username.setText(m.getActiveUserNickname());

//        seguiti/amici che si sta seguendo
        tv_friends = v.findViewById(R.id.txt_seguiti);
        tv_friends.setText(String.valueOf(friendsCount));

//        bottone cambia immagine
        Button changeProPic = v.findViewById(R.id.btn_change_profile_pic);
        changeProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
            }
        });

        getPosts();

        return v;
    }

    private void checkStoragePermissions(){
//        permission isn't granted: prompt the user
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
//            permissions were already granted, proceed normally
            changeProfilePic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    changeProfilePic();
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void changeProfilePic(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String picturePath= pictureDirectory.getPath();

        Uri actualPictures= Uri.parse(picturePath);
        photoPickerIntent.setDataAndType(actualPictures,"image/*");
        startActivityForResult(photoPickerIntent, 20);
    }

    @Override
    // relativo al photoPickerIntent, serve per aggiunge l'immagine
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 20 & resultCode == Activity.RESULT_OK) {
            Uri imageURI = data.getData();

            if(imageURI !=null){
                proPic.setImageURI(imageURI);

//              aggiorna su server e Model
                String encoded= imageHandler.encodeFromUri(imageURI);
                serverService.updatePicture(m.getSessionID(), encoded);

                m.setActiveUserImg(encoded);
            }
        }
    }

    private void getPosts(){
        PostsAdapter postsAdapter= new PostsAdapter(context, R.layout.item_user_posts_item, m.getActivePosts());

        postsListView.setAdapter(postsAdapter);
    }

    // chiamato da ServerService
    public void onRefreshUserInfo(){
        ((Navigation)getActivity()).stopRefreshAnimation();
        getPosts();
    }
}
