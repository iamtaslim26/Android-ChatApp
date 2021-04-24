package com.kgec.androidchatapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.androidchatapp.Adapter.MessageAdapter;
import com.kgec.androidchatapp.Adapter.UsersAdapter;
import com.kgec.androidchatapp.Class.Chat;
import com.kgec.androidchatapp.Class.ChatList;
import com.kgec.androidchatapp.Class.Users;
import com.kgec.androidchatapp.R;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private View chatFragmentsView;
    private RecyclerView chats_list_view;

    private UsersAdapter usersAdapter;
    private List<Users>mUsers;
    private List<ChatList>usersList;

    private FirebaseUser firebaseUser;
    private String current_user_id;
    private DatabaseReference ChatsRef,ChatListRef,UsersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatFragmentsView= inflater.inflate(R.layout.fragment_chats, container, false);

        chats_list_view=chatFragmentsView.findViewById(R.id.chat_name_view);
        chats_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        chats_list_view.setHasFixedSize(true);

        usersList=new ArrayList<>();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        current_user_id=firebaseUser.getUid();
        ChatsRef= FirebaseDatabase.getInstance().getReference().child("Message");
        ChatListRef= FirebaseDatabase.getInstance().getReference().child("ChatList").child(current_user_id);
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");



    /*    ChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Chat chat=snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(current_user_id)){

                        usersList.add(chat.getReceiver());;
                    }
                    else if (chat.getReceiver().equals(current_user_id)){
                        usersList.add(chat.getSender());
                    }
                }

                readChats();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





     */

        ChatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersList.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    ChatList chatList=snapshot.getValue(ChatList.class);

                    usersList.add(chatList);

                }

                readChatList();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return chatFragmentsView;
    }

    private void readChatList() {

        mUsers=new ArrayList<>();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Users users=snapshot.getValue(Users.class);

                    for (ChatList chatList:usersList){
                        if (users.getUid().equals(chatList.getId())){
                            mUsers.add(users);
                        }
                    }
                }

                usersAdapter=new UsersAdapter(getContext(),mUsers,true);
                chats_list_view.setAdapter(usersAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

  /*  private void readChats() {

        mUsers=new ArrayList<>();

        DatabaseReference UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Users users=snapshot.getValue(Users.class);

                    for (String id:usersList){
                        if (users.getUid().equals(id)){
                            if (mUsers.size()!=0){
                                for (Users users1:mUsers){
                                    if (!users.getUid().equals(users1.getUid())){
                                        mUsers.add(users);
                                    }
                                }
                            }else {
                                mUsers.add(users);
                            }
                        }
                    }
                }

                usersAdapter=new UsersAdapter(getContext(),mUsers,true);
                chats_list_view.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

   */
}