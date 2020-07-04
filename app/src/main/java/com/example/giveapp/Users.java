package com.example.giveapp;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Users implements Serializable {
    public String fName;
    public String imageUrl;
    public String email;

    public String id; // added

//    @Exclude private String id;

    public String getName() {
        return fName;
    }

    public void setName(String fName) {
        this.fName = fName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Users() {
    }

    public Users(String fName, String email, String imageUrl, String id) { // added

        this.fName = fName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.id = id;

    }

}
