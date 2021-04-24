package com.kgec.androidchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView imageview;
    private EditText user_status,user_name,user_country;
    private Button save_btn;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private DatabaseReference SettingsRef;
    private StorageReference UsersProfileImageRef;
    private final static int gallery_pick=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageview=findViewById(R.id.set_up_image);
        user_country=findViewById(R.id.set_up_country_name);
        user_name=findViewById(R.id.set_up_username1);
        user_status=findViewById(R.id.set_up_status);
        save_btn=findViewById(R.id.set_up_save_button1);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        SettingsRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UsersProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");


        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery_intent=new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent,gallery_pick);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountInfo();
            }
        });


        SettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("country")&& dataSnapshot.hasChild("status")){

                        String COUNTRY=dataSnapshot.child("country").getValue().toString();
                        String STATUS=dataSnapshot.child("status").getValue().toString();

                        user_country.setText(COUNTRY);
                        user_status.setText(STATUS);
                    }

                    String USERNAME=dataSnapshot.child("username").getValue().toString();

                    String PROFILE_IMAGE=dataSnapshot.child("ImageUrl").getValue().toString();

                    user_name.setText(USERNAME);

                    Picasso.get().load(PROFILE_IMAGE).placeholder(R.drawable.profile).into(imageview);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SaveAccountInfo() {

        String username=user_name.getText().toString();
        String country=user_country.getText().toString();
        String status=user_status.getText().toString();

        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(country) && TextUtils.isEmpty(status)){

            Toast.makeText(this, "Please Fill up the rest. ... ", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String,Object>map=new HashMap<>();
            map.put("username",username);
            map.put("country",country);
            map.put("status",status);

            SettingsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        Toast.makeText(SettingsActivity.this, "Update Succesfully.......", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        String e=task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Failed.....      "+e, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==gallery_pick && resultCode==RESULT_OK && data!=null){

            Uri ImageUri=data.getData();


            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


            }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){

                Uri resultUri=result.getUri();

                StorageReference filepath=UsersProfileImageRef.child(current_user_id+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            String downloadUrl=task.getResult().getDownloadUrl().toString();
                            Toast.makeText(SettingsActivity.this, "Image is stored in Firebase Storage....", Toast.LENGTH_SHORT).show();

                            SettingsRef.child("ImageUrl").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){


                                        Toast.makeText(SettingsActivity.this, "Image is saved into the database...", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                        String  e=task.getException().getMessage();
                                        Toast.makeText(SettingsActivity.this, "Failed....     "+e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {

                            String e = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Failed....     "+e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
            else {

                Toast.makeText(this, "Failed to crop Image......", Toast.LENGTH_SHORT).show();
            }
        }
    }
}