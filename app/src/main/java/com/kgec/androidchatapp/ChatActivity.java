package com.kgec.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.androidchatapp.Adapter.MessageAdapter;
import com.kgec.androidchatapp.Class.Chat;
import com.kgec.androidchatapp.Class.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String message_receiver_id;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,ChatRef;
    private String current_user_id;
    private TextView User_name;
    private CircleImageView imageview;


    private EditText input_text_msg;
    private ImageButton send_msg_btn;

    private RecyclerView chat_list;
    private MessageAdapter messageAdapter;
    private List<Chat>mChat;

    private ValueEventListener seenListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        message_receiver_id=getIntent().getExtras().get("visit_user_id").toString();

        imageview=findViewById(R.id.profile_image_chat);
        User_name=findViewById(R.id.profile_name_chat);
        input_text_msg=findViewById(R.id.input_message);
        send_msg_btn=findViewById(R.id.send_message_btn);

        chat_list=findViewById(R.id.chat_list);
        chat_list.setLayoutManager(new LinearLayoutManager(this));
        chat_list.setHasFixedSize(true);


        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(message_receiver_id);
        ChatRef=FirebaseDatabase.getInstance().getReference().child("Message");


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String username=dataSnapshot.child("username").getValue().toString();
                    String profileImage=dataSnapshot.child("ImageUrl").getValue().toString();

                    User_name.setText(username);
                    if (profileImage.equals("default")){

                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_launcher_background).into(imageview);
                    }
                    else {
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(imageview);
                    }

                    readMessage(current_user_id,message_receiver_id,profileImage);



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        seenMessage(message_receiver_id);


        send_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=input_text_msg.getText().toString();
                SendMessage(current_user_id,message_receiver_id,message);
            }
        });


    }

    private void SendMessage(String current_user_id, String message_receiver_id, String message) {

        if (TextUtils.isEmpty(message)){

            Toast.makeText(this, "Please Write your message......", Toast.LENGTH_SHORT).show();
        }else {
            DatabaseReference ChatRef=FirebaseDatabase.getInstance().getReference();

            HashMap<String,Object>map=new HashMap<>();
            map.put("message",message);
            map.put("sender",current_user_id);
            map.put("receiver",message_receiver_id);
            map.put("isSeen",false);

            ChatRef.child("Message").push().setValue(map);

            DatabaseReference ChatListRef=FirebaseDatabase.getInstance().getReference().child("ChatList")
                    .child(current_user_id)
                    .child(message_receiver_id);



            ChatListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()){

                        ChatListRef.child("id").setValue(message_receiver_id);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        input_text_msg.setText("");



    }

    private void readMessage(String myId,String userId,String imageUrl){

        mChat=new ArrayList<>();

        ChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mChat.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat != null;

                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId)
                            || chat.getSender().equals(myId)&&chat.getReceiver().equals(userId)){

                        mChat.add(chat);
                    }

                }

                messageAdapter=new MessageAdapter(ChatActivity.this,mChat,imageUrl);
                chat_list.setAdapter(messageAdapter);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void seenMessage(String message_receiver_id){

       DatabaseReference ChatsRef= FirebaseDatabase.getInstance().getReference().child("Message");

       seenListener=ChatsRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                   Chat chat=snapshot.getValue(Chat.class);
                   if (chat.getReceiver().equals(current_user_id) && chat.getSender().equals(message_receiver_id)){

                       HashMap<String ,Object>map=new HashMap<>();
                       map.put("isSeen",true);

                       snapshot.getRef().updateChildren(map);
                   }
               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

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
        ChatRef.removeEventListener(seenListener);
        userstatus("offline");
    }
}