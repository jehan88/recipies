package com.jehan.recipes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Loader extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
                getUserInfo();

        } else {
            startActivity(new Intent(Loader.this, loginActivity.class));
            finish();
        }
    }

    private void getUserInfo(){
        FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Controller.user = dataSnapshot.getValue(User.class);
                        System.out.println("User : "+Controller.user);
                        goHome();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Loader.this, "Error no Connection :" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void goHome(){
        startActivity(new Intent(Loader.this, MainActivity.class));
        finish();
    }
}
