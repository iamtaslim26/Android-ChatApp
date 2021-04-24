package com.kgec.androidchatapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kgec.androidchatapp.Adapter.UsersAdapter;
import com.kgec.androidchatapp.R;
import com.kgec.androidchatapp.Class.Users;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private View groupFragmentView;
    private RecyclerView users_list;
    private UsersAdapter usersAdapter;
    private List<Users>mUsers;

    private EditText search_users;




    public UsersFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView= inflater.inflate(R.layout.fragment_users, container, false);

        users_list=groupFragmentView.findViewById(R.id.users_list);
        users_list.setLayoutManager(new LinearLayoutManager(getContext()));
        users_list.setHasFixedSize(true);

        search_users=groupFragmentView.findViewById(R.id.search_users);


       mUsers=new ArrayList<>();

       readUsers();


       search_users.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

                SearchUser(s.toString().toLowerCase());
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });


        return groupFragmentView;



    }

    private void SearchUser(String s) {

        FirebaseUser fUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        Query query=UsersRef.orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Users users=snapshot.getValue(Users.class);

                    assert users != null;
                    assert fUser != null;

                    if (!users.getUid().equals(fUser.getUid())){

                        mUsers.add(users);
                    }
                }
                usersAdapter=new UsersAdapter(getContext(),mUsers,true);
                users_list.setAdapter(usersAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void readUsers() {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Users users = snapshot.getValue(Users.class);

                        assert users != null;
                        assert firebaseUser != null;

                        if (!users.getUid().equals(firebaseUser.getUid())) {
                            mUsers.add(users);
                        }

                    }


                    usersAdapter = new UsersAdapter(getContext(), mUsers, true);
                    users_list.setAdapter(usersAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
/*
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users>options=new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(UsersRef,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,UsersAdapter>adapter=new FirebaseRecyclerAdapter<Users, UsersAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersAdapter holder, int position, @NonNull Users model) {



                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.profile_image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id=getRef(position).getKey();
                        if (!visit_user_id.equals(current_user_id)){

                            Intent intent=new Intent(getContext(), ChatActivity.class);
                            intent.putExtra("visit_user_id",visit_user_id);
                            startActivity(intent);

                        }
                        else {

                            Toast.makeText(getContext(), "This is Your profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @NonNull
            @Override
            public UsersAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_layout,parent,false);
                UsersAdapter usersAdapter=new UsersAdapter(view);
                return usersAdapter;


            }
        };
        users_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersAdapter extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView username;

        public UsersAdapter(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.user_profile_image);
            username=itemView.findViewById(R.id.user_profile_name);
        }
    }

   */
}