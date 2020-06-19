package com.example.giveapp;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Challenge implements Serializable {

    @Exclude private String id;

    private String title, shortDesc, longDesc, image;

    public Challenge() {

    }

    public Challenge(String title, String shortDesc, String longDesc, String image) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public String getImage() {
        return image;
    }
}
