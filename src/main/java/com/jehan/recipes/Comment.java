package com.jehan.recipes;


public class Comment {
    String user,comment,file,type;

    public Comment(){}
    public Comment(String user,String comment){
        this.user=user;
        this.comment=comment;
        this.file="";
        this.type="";
    }
    public Comment(String user,String comment,String file,String type){
        this.user=user;
        this.comment=comment;
        this.file=file;
        this.type=type;
    }
}
