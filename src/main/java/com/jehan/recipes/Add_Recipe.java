package com.jehan.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Add_Recipe extends AppCompatActivity {
    public FirebaseAuth auth;
    EditText inputName, inputDesc;
    Button choos, add, cancel;
    Spinner cats;
    ArrayAdapter<Cat> catArrayAdapter;
    ImageView imv;
    private Uri filePath;
    ProgressBar progressBar;
    StorageReference storageReference;
    FirebaseStorage storage;
    private static final int READ_REQUEST_CODE = 42;
    FirebaseDatabase fdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        inputName = (EditText) findViewById(R.id.name_input);
        inputDesc = (EditText) findViewById(R.id.desc_input);
        choos = (Button) findViewById(R.id.img_btn);
        add = (Button) findViewById(R.id.save_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);
        imv=(ImageView)findViewById(R.id.imageView);
        cats=(Spinner) findViewById(R.id.choose_cat);
        imv.setVisibility(View.GONE);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        fdb=FirebaseDatabase.getInstance();

        catArrayAdapter=new ArrayAdapter<Cat>(this,android.R.layout.simple_spinner_item,Cat.values());
        cats.setAdapter(catArrayAdapter);

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "Sorry you need to login first !!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Add_Recipe.this, SightUpActivity.class));
            finish();
        }

        choos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);


            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                uploadImage();

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Add_Recipe.this, MainActivity.class));
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imv.setVisibility(View.VISIBLE);
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void uploadImage(){

        if(filePath!=null){
            final StorageReference str=storageReference.child("images/"+ UUID.randomUUID().toString());
            str.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        String name=inputName.getText().toString();
                        String desc=inputDesc.getText().toString();
                        String img_name=str.getName();
                        Recipe recipe=new Recipe(name,img_name,desc,auth.getCurrentUser().getUid(),Controller.user.name,(Cat)cats.getSelectedItem());
                        add_recipe(recipe);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    void add_recipe(Recipe recipe){
        fdb.getReference().child("Recipes").child(String.valueOf(Controller.recipes.size())).setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Controller.recipes.clear();
                    Controller.fav_recipes.clear();
                    startActivity(new Intent(Add_Recipe.this,MainActivity.class));
                    Toast.makeText(getApplicationContext(),"Recipe Was Added",Toast.LENGTH_LONG).show();

                }
            }
        });
    }



}
