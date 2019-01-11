package com.jehan.recipes;


import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
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


public class FavAdapter extends RecyclerView.Adapter<MyViewFavHolder> {
    private ArrayList<Recipe>recipes=new ArrayList<Recipe>();
    public FavAdapter(){
        super();
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public FavAdapter(ArrayList<Recipe> arrayList){
        super();
       this.recipes=arrayList;
    }
    @Override
    public MyViewFavHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.recipe_fav,parent,false);
        return new MyViewFavHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewFavHolder holder, int position) {
        Recipe recipe=recipes.get(position);
        if(recipe.img!=""){
            FirebaseStorage.getInstance().getReference().child("/images/"+recipe.img).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.recipe_img);
                        }
                    });
        }

        holder.title.setText(recipe.name);
//        holder.name_view.setText(recipe.user_name);
        holder.rate_view.setText(String.valueOf(recipe.sum()));



    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}


class MyViewFavHolder extends RecyclerView.ViewHolder{
    TextView title,name_view,rate_view;
    ImageView recipe_img;
    ConstraintLayout res_row;
    public MyViewFavHolder(View v){
        super(v);
        title=(TextView)v.findViewById(R.id.titleView);
//        name_view=(TextView)v.findViewById(R.id.name_view);
        recipe_img=(ImageView)v.findViewById(R.id.recipe_img);
        rate_view=(TextView)v.findViewById(R.id.rateView);
//        res_row=(ConstraintLayout)v.findViewById(R.id.res_row);

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
