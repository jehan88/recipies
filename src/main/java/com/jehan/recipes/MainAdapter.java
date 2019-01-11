package com.jehan.recipes;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Ali on 11/19/2018.
 */

public class MainAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private String[] titles={"first","second"};
    private ArrayList<Recipe>recipes;
    public MainAdapter(){
        super();
    }
    public MainAdapter(ArrayList<Recipe> arrayList){
        super();
        recipes=arrayList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.recipe_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Recipe recipe=recipes.get(position);
        if(recipe.img!=""){
            FirebaseStorage.getInstance().getReference().child("/images/"+recipe.img).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.recipe_img);
                            System.out.println(uri);
                        }
                    });
        }

        holder.title.setText(recipe.name);
        holder.name_view.setText(recipe.user_name);
        holder.rate_view.setText(String.valueOf(recipe.sum()));
        System.out.println(recipe.comments.size());


    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}


class MyViewHolder extends RecyclerView.ViewHolder{
    TextView title,name_view,rate_view;
    ImageView recipe_img;
    public MyViewHolder(View v){
        super(v);
        title=(TextView)v.findViewById(R.id.titleView);
        name_view=(TextView)v.findViewById(R.id.name_view);
        recipe_img=(ImageView)v.findViewById(R.id.recipe_img);
        rate_view=(TextView)v.findViewById(R.id.rateView);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=getAdapterPosition();
                Intent intent=new Intent(view.getContext(),Recipe_Detals.class);
                intent.putExtra("position",String.valueOf(position));
                view.getContext().startActivity(intent);

            }
        });
    }


        }
