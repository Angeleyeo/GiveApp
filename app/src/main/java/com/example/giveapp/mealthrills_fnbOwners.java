package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class mealthrills_fnbOwners extends AppCompatActivity {

    Button mPost, mItems;
    ImageButton prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealthrills_fnb_owners);

        mPost = findViewById(R.id.addNewPostBtn);
        mItems = findViewById(R.id.itemsReservedBtn);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mealthrills_fnbOwners.this, mealthrills_fnb_post.class));
            }
        });

        mItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mealthrills_fnbOwners.this, reservedFood.class));
            }
        });

        prevButton = findViewById(R.id.prevBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MealThrillsUser.class);
                startActivity(i);
            }
        });


    }
}