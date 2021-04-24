package com.kgec.androidchatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.androidchatapp.ChatActivity;
import com.kgec.androidchatapp.Class.Chat;
import com.kgec.androidchatapp.R;
import com.kgec.androidchatapp.Class.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context mContext;
    private List<Users>mUsers;
    private boolean isChat;
    private String theLastMessage;

    public UsersAdapter(Context mContext,List<Users>mUsers,boolean isChat){

        this.mContext=mContext;
        this.mUsers=mUsers;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_layout,parent,false);

        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users=mUsers.get(position);
        holder.username.setText(users.getUsername());
        if (users.getImageUrl().equals("default")){

            Picasso.get().load(users.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.profile_image);

        }else {

            Picasso.get().load(users.getImageUrl()).placeholder(R.drawable.profile).into(holder.profile_image);
        }

        if (isChat){

            LastMessage(users.getUid(),holder.last_msg);
        }
        else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (isChat){

            if (users.getUserstatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.INVISIBLE);
            }
            else {
                holder.img_on.setVisibility(View.INVISIBLE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else {

            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext, ChatActivity.class);
                intent.putExtra("visit_user_id",users.getUid());
                mContext.startActivity(intent);
            }
        });


    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image,img_on,img_off;
        TextView username,last_msg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.user_profile_image);
            username=itemView.findViewById(R.id.user_profile_name);
            img_off=itemView.findViewById(R.id.img_off);
            img_on=itemView.findViewById(R.id.img_on);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }

    // Check For Last Message...

    private void LastMessage(String userId, TextView last_msg) {

        theLastMessage="default";

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser()        ;
        String currentuserId=firebaseUser.getUid();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Chat chat=snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(currentuserId) && chat.getSender().equals(userId)
                    || chat.getReceiver().equals(userId)&& chat.getSender().equals(currentuserId)){

                        theLastMessage=chat.getMessage();
                    }
                }
                switch (theLastMessage){

                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage="default";



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
