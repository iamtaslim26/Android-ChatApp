package com.kgec.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.androidchatapp.Adapter.TabAccessorAdapter;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private DatabaseReference UsersRef;
    private Toolbar mToolbar;

    private TabLayout myTablayout;
    private ViewPager myViewPager;
    private TabAccessorAdapter tabAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        myViewPager=findViewById(R.id.view_pager);
        myTablayout=findViewById(R.id.tab_layout);
        tabAccessorAdapter=new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(tabAccessorAdapter);
        myTablayout.setupWithViewPager(myViewPager);


        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.profile_name);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String name=dataSnapshot.child("username").getValue().toString();
                    String image=dataSnapshot.child("ImageUrl").getValue().toString();

                    username.setText(name);

                    if (image.equals("default")){

                        profile_image.setImageResource(R.drawable.ic_launcher_background);
                    }
                    else {
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profile_image);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout:
                mAuth.signOut();
                SendUserToStartActivity();
                return true;

            case R.id.settings_page:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                return true;
        }
        return false;
    }

    private void SendUserToStartActivity() {

//        Intent intent=new Intent(getApplicationContext(),StartActivity.class.);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();

        startActivity(new Intent(getApplicationContext(),StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void userstatus(String userstatus){
        HashMap<String,Object>map=new HashMap<>();
        map.put("userstatus",userstatus);

        UsersRef.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();

        userstatus("online");
    }

    @Override
    protected void onPause() {

        super.onPause();
        userstatus("offline");
    }
}