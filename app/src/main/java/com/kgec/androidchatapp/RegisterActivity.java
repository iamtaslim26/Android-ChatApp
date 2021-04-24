package com.kgec.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText email_text,username_text,password_text;
    private Button register_btn;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_text=findViewById(R.id.et_email);
        username_text=findViewById(R.id.et_username);
        password_text=findViewById(R.id.et_password);
        register_btn=findViewById(R.id.register_btn);

        mAuth=FirebaseAuth.getInstance();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=email_text.getText().toString();
                String username=username_text.getText().toString();
                String password=password_text.getText().toString();

                CreateAccount(email,password,username);
            }
        });

    }

    private void CreateAccount(String email, String password, String username) {

        if (TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)||TextUtils.isEmpty(username)){

            Toast.makeText(this, "Please Fill up the Form. ... ", Toast.LENGTH_SHORT).show();
        }
        else {


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        SaveUserInfo(username);
                        SendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account Created. . . .", Toast.LENGTH_SHORT).show();


                    }
                    else {

                        String e=task.getException().getMessage();

                        Toast.makeText(RegisterActivity.this, "Failed......    "+e, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SaveUserInfo(String username) {

        current_user_id=mAuth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);




        HashMap<String,Object>map=new HashMap<>();
        map.put("uid",current_user_id);
        map.put("username",username);
        map.put("ImageUrl","default");
        map.put("userstatus","offline");
        map.put("search",username.toLowerCase());

        UserRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(RegisterActivity.this, "Data is saved", Toast.LENGTH_SHORT).show();
                }
                else {


                    String e=task.getException().getMessage();

                    Toast.makeText(RegisterActivity.this, "Failed......    "+e, Toast.LENGTH_LONG).show();

                }
            }
        });


    }
}