package com.example.giveapp;

import android.widget.ImageView;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Beneficiary implements Serializable {

    @Exclude private String id;

    private String name, logo, desc;

    private int userDonation;

    public Beneficiary() {

    }

    public Beneficiary(String ben_name, String ben_logo, String ben_desc, int ben_userDonation) {
        this.name = ben_name;
        this.logo = ben_logo;
        this.desc = ben_desc;
        this.userDonation = ben_userDonation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getDesc() {
        return desc;
    }

    public int getUserDonation() {
        return userDonation;
    }
}


