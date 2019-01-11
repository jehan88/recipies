package com.jehan.recipes;
public enum Cat{
    HotFood("HotFood"),
    FastFood("FastFood"),
    WestFood("WestFood"),
    EastFood("EastFood"),
    Others("Others");


    private String theState;

    Cat(String aState) {
        theState = aState;
    }

    @Override public String toString() {
        return theState;
    }
        }