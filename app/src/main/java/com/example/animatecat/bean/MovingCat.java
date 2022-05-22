package com.example.animatecat.bean;

import com.example.animatecat.R;

import java.io.Serializable;

public class MovingCat implements Serializable {
    private float poids;
    private String name;
    private int picture_cat;


    public MovingCat(int poids, String name) {
        this.poids = poids;
        this.name = name;
        this.picture_cat= R.drawable.cat;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPicture_cat() {
        return picture_cat;
    }

    public void setPicture_cat(int picture_cat) {
        this.picture_cat = picture_cat;
    }
}
