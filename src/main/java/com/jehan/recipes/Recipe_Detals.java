package com.jehan.recipes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class Recipe_Detals extends AppCompatActivity {
    private Intent intent;
    private String position;
    private Recipe recipe;
    private TextView name,desc;
    private ImageView img;
    private ImageButton add_commant,add_image,rate_btn,edit_btn,videw_btn;
    private EditText comment_input;
    private RecyclerView comments_list;
    private CommentAdapter commentAdapter;
    private final int READ_REQUEST_CODE=1;
    private FirebaseAuth auth;
    private SeekBar rateBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe__detals);
        intent = getIntent();
        position = intent.getStringExtra("position");
        recipe = Controller.recipes.get(Integer.valueOf(position));
        name = (TextView) findViewById(R.id.name_label);
        desc = (TextView) findViewById(R.id.recipe_desc);
        img = (ImageView) findViewById(R.id.recipe_img);
        add_commant = (ImageButton) findViewById(R.id.add_comment_btn);
        add_image=(ImageButton)findViewById(R.id.add_image_comment);
        rate_btn=(ImageButton)findViewById(R.id.rate_btn);
        edit_btn=(ImageButton)findViewById(R.id.edit_btn);
        videw_btn=(ImageButton)findViewById(R.id.video_btn);
        comments_list=(RecyclerView)findViewById(R.id.comments_list);
        auth=FirebaseAuth.getInstance();
        name.setText(recipe.name);
        desc.setText(recipe.desc);
        commentAdapter=new CommentAdapter(recipe.comments);
        comments_list.setLayoutManager(new LinearLayoutManager(this));
        comments_list.setAdapter(commentAdapter);
        comments_list.addItemDecoration(new DividerItemDecoration(comments_list.getContext(),1));


        if (recipe.img != "") {
            FirebaseStorage.getInstance().getReference().child("images/" + recipe.img)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(img);
                }
            });
        }



        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Recipe_Detals.this,edit_recipe.class);
                intent.putExtra("position",position);
                startActivity(intent);
                finish();
            }
        });

        add_commant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choos_image();
            }
        });
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_rate_dilog();
            }


        });
        videw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choos_video();
            }
        });
    }

    private void start_rate_dilog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate The Recipe");
        rateBar=new SeekBar(this);
        rateBar.setMax(5);
        builder.setView(rateBar);

        builder.setNegativeButton("rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               insert_rate();
            }
        });

        AlertDialog ad=builder.create();
        ad.show();
    }

        void insert_rate(){
            recipe.rates.add(new Rate(Float.valueOf(rateBar.getProgress()),auth.getCurrentUser().getUid()));
            recipe.sum();
            updateRecipe();
        }
        void alert(){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Comment");
            comment_input=new EditText(this);
            builder.setView(comment_input);

            builder.setNegativeButton("Insert", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    insert_comment();
                }
            });

            AlertDialog ad=builder.create();
            ad.show();

            //View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.text_inpu_password, (ViewGroup) getView(), false);
        }

        void insert_comment(){
            Comment comment=new Comment(Controller.user.name,comment_input.getText().toString());
            recipe.comments.add(comment);
            updateRecipe();
        }

    private void updateRecipe() {
        FirebaseDatabase.getInstance().getReference().child("Recipes").child(position).setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    commentAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"Comment added",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void choos_image(){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), READ_REQUEST_CODE);
        }
    void choos_video(){
        Intent intent = new Intent();
        intent.setType("video/mp4");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            final String type=getContentResolver().getType(filePath);
            System.out.println(type);
                final StorageReference str=FirebaseStorage.getInstance().getReference().child("files/"+ UUID.randomUUID().toString());
                str.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String file = str.getName();
                            Comment comment = new Comment(Controller.user.name, "", file,type);
                            recipe.comments.add(comment);
                            updateRecipe();
                        }


                    }
                });




        }
    }
}



