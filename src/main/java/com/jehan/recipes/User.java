package com.jehan.recipes;

/**
 * Created by ALI on 11/18/2018.
 */

public class User {
    public String name,email;
    public Cat fav=Cat.Others;
    public User(){}

    public User(String name, String email,Cat fav) {
        this.name = name;
        this.email = email;
        this.fav=fav;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Cat getFav() {
        return fav;
    }

    public void setFav(Cat fav) {
        this.fav = fav;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", fav=" + fav +
                '}';
    }
}
