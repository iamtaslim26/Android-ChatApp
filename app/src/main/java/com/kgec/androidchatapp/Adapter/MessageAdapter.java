package com.kgec.androidchatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kgec.androidchatapp.Class.Chat;
import com.kgec.androidchatapp.Class.Users;
import com.kgec.androidchatapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private String imageUrl;
    private Context mContext;
    private List<Chat>mChat;

    private final static int MESSAGE_LEFT=0;
    private final static int MESSAGE_RIGHT=1;

    private FirebaseUser firebaseUser;
    private String current_user_id;

    public MessageAdapter(Context mContext, List<Chat> mChat,String imageUrl){

        this.mContext=mContext;
        this.mChat=mChat;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MESSAGE_RIGHT){

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());




       if (imageUrl.equals("default")){

            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(holder.chat_image);

        }else {

            Picasso.get().load(imageUrl).into(holder.chat_image);
        }


       if (position==mChat.size()-1){

           if (chat.getisSeen()){
               holder.msg_txt.setText("Seen");
           }
           else {
               holder.msg_txt.setText("Delivered");
           }
       }
       else {

           holder.msg_txt.setVisibility(View.GONE);
       }




    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chat_image;
        TextView show_message,msg_txt;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            chat_image=itemView.findViewById(R.id.profile_message_image);
            show_message=itemView.findViewById(R.id.show_message);
            msg_txt=itemView.findViewById(R.id.txt_seen);



        }


    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        current_user_id=firebaseUser.getUid();


        if (mChat.get(position).getSender().equals(current_user_id)){

                return MESSAGE_RIGHT;
        }
        else {
            return MESSAGE_LEFT;
        }
    }
}
