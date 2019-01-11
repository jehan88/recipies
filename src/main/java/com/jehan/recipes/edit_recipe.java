package com.jehan.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class edit_recipe extends AppCompatActivity {
    int position;
    EditText inputName, inputDesc;
    Button choos, save, cancel;
    ImageView imv;
    private Uri filePath;
    ProgressBar progressBar;
    StorageReference storageReference;
    FirebaseStorage storage;
    Recipe recipe;
    private static final int READ_REQUEST_CODE = 42;
    FirebaseDatabase fdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        position=Integer.valueOf(getIntent().getStringExtra("position"));
        recipe=Controller.recipes.get(position);
        inputName = (EditText) findViewById(R.id.name_input);
        inputDesc = (EditText) findViewById(R.id.desc_input);
        choos = (Button) findViewById(R.id.img_btn);
        save = (Button) findViewById(R.id.save_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);
        imv=(ImageView)findViewById(R.id.imageView);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        fdb=FirebaseDatabase.getInstance();
        if (recipe.img != "") {
            FirebaseStorage.getInstance().getReference().child("images/" + recipe.img)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(imv);
                }
            });
        }
        inputName.setText(recipe.name);
        inputDesc.setText(recipe.desc);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(edit_recipe.this,MainActivity.class));
                finish();
            }
        });



        choos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
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
            final StorageReference str=storageReference.child("images/"+ recipe.img==""?UUID.randomUUID().toString():recipe.img);
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

                            }
                        }
                    });
        }else{
            save_recipe(recipe.img);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void save_recipe(String name) {
        recipe.name=inputName.getText().toString();
        recipe.desc=inputDesc.getText().toString();
        recipe.img=name;
        Controller.recipes.set(position,recipe);
        update_recipes();
    }

    private void update_recipes() {
        fdb.getReference().child("Recipes").child(String.valueOf(position)).setValue(recipe)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Controller.recipes.clear();;
                           Controller.fav_recipes.clear();
                           startActivity(new Intent(edit_recipe.this,MainActivity.class));
                           finish();
                           Toast.makeText(getApplicationContext(),"Saved !",Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }


}
