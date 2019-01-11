package com.jehan.recipes;

import java.util.ArrayList;


public class Recipe {
    public String name,img,desc,UID,user_name;
    public ArrayList<Comment> comments =new ArrayList<Comment>();
    public float rate=0;
    public Cat cat=Cat.Others;
    ArrayList<Rate> rates=new ArrayList<Rate>();
    public Recipe(){

    }

    public Recipe(String name, String img, String desc,String UID,String user_name,Cat cat) {
        this.name = name;
        this.img = img;
        this.desc = desc;
        this.UID=UID;
        this.user_name=user_name;
        this.cat=cat;

    }

    public float sum(){
        if(rates.size()<=0) return  0;
        float sm=0;
        for (Rate rt: rates
             ) {
            sm+=rt.rate;
        }
        return sm/rates.size();

    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", desc='" + desc + '\'' +
                ", UID='" + UID + '\'' +
                ", user_name='" + user_name + '\'' +
                ", comments=" + comments +
                ", rate=" + rate +
                ", cat=" + cat +
                ", rates=" + rates +
                '}';
    }
}
