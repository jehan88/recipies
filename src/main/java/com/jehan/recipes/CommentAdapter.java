package com.jehan.recipes;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class CommentAdapter extends RecyclerView.Adapter<CommentsViewHolder>{
    private ArrayList<Comment> comments;
    private String uuu="https://firebasestorage.googleapis.com/v0/b/recipes-b4214.appspot.com/o/files%2F35697041-8dfc-4581-8a72-23a9f2dcbe96?alt=media&token=3a7f6be3-35b4-49f3-9cb6-5a2af289cf2d";
    public CommentAdapter(ArrayList<Comment> comments){
        this.comments=comments;
    }
    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.commet_row,parent,false);
        return new CommentsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder holder, int position) {
        final Comment comment=comments.get(position);
        holder.name.setText(comment.user);
                if(comment.type!=null && comment.type!=""){
                    holder.comment.setVisibility(View.GONE);
                    FirebaseStorage.getInstance().getReference("files/"+comment.file).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if(!comment.type.equals("video/mp4")){
                                       holder.wv.setVisibility(View.GONE);
                                        Picasso.get().load(uri).into(holder.img);
                                        System.out.println("----------- img ||||||||"+uri);
                                    }else{
                                        MediaController mc=new MediaController(holder.view.getContext());
                                        holder.img.setVisibility(View.GONE);
                                        WebChromeClient webChromeClient=new WebChromeClient();
                                        WebViewClient wvClient=new WebViewClient();
                                        holder.wv.setWebChromeClient(webChromeClient);
                                        holder.wv.setWebViewClient(wvClient);
                                        holder.wv.getSettings().setJavaScriptEnabled(true);
                                        holder.wv.getSettings().setPluginState(WebSettings.PluginState.ON);
                                        holder.wv.loadUrl(uri.toString());
//

                                    }
                                }
                            });
                }else{
                    holder.img.setVisibility(View.GONE);
                    holder.wv.setVisibility(View.GONE);
                    holder.comment.setText(comment.comment);
                }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

class CommentsViewHolder extends RecyclerView.ViewHolder{
    TextView name,comment;
    ImageView img;
    View view;
    WebView wv;
    public CommentsViewHolder(View view){
        super(view);
        this.view=view;
        name=(TextView)view.findViewById(R.id.name);
        comment=(TextView)view.findViewById(R.id.comment);
        img=(ImageView) view.findViewById(R.id.img);
        wv=(WebView)view.findViewById(R.id.webs);
    }

}