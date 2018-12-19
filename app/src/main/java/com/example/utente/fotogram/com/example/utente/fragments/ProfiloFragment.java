package com.example.utente.fotogram.com.example.utente.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente.fotogram.Object_classes.ImageHandler;
import com.example.utente.fotogram.Object_classes.Model;
import com.example.utente.fotogram.Object_classes.PostsAdapter;
import com.example.utente.fotogram.Object_classes.ServerService;
import com.example.utente.fotogram.R;

public class ProfiloFragment extends Fragment {

    private Model m;

    private int PICK_PHOTO = 100;
    public ImageView proPic;
    private Context context;
    private ServerService serverService;
    private ImageHandler imageHandler;

    public ProfiloFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profilo, container, false);

        context= getContext();
        m= Model.getInstance();
        serverService= ServerService.getInstance(context);
        imageHandler= new ImageHandler(context);

        serverService.getUserInfo(m.getSessionID(), m.getActiveUserNickname());
        proPic= v.findViewById(R.id.img_profile_pic);

        String image= m.getActiveUserImg();

        if(image != null){
            proPic.setImageBitmap(imageHandler.decodeString(image));
        }// altrimenti c'è il placeholder

        TextView username= v.findViewById(R.id.txt_username);
        username.setText(m.getActiveUserNickname());

        Button changeProPic = v.findViewById(R.id.btn_change_profile_pic);
        changeProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissions();
                changeProfilePic();
            }
        });

        getPosts(v);

        return v;
    }

    private void checkStoragePermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }//        |
    }//            |
    //             V
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                }else{
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void changeProfilePic(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO);
        //                     |
    }//                        |
//                             V
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PICK_PHOTO & resultCode == Activity.RESULT_OK) {
            Uri imageURI = data.getData();

            if(imageURI !=null){
                proPic.setImageURI(imageURI);

//              miei metodi
                String encoded= imageHandler.encodeFromUri(imageURI);
                serverService.updatePicture(encoded);
            }
        }
    }

    private void getPosts(View v){
        // TODO: chiamata server che ottiene ArrayList di posts


        ListView postsListView= v.findViewById(R.id.personal_posts);
        //PostsAdapter postsAdapter= new PostsAdapter(context, R.layout.bacheca_post_list_item, posts);

        //postsListView.setAdapter();
    }
}
