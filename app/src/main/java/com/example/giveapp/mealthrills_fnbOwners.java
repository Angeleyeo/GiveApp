package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mealthrills_fnbOwners extends AppCompatActivity {

    Button mPost, mItems, mMarketplace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealthrills_fnb_owners);

        mPost = findViewById(R.id.addNewPostBtn);
        mItems = findViewById(R.id.itemsReservedBtn);
        mMarketplace = findViewById(R.id.marketplace);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mealthrills_fnbOwners.this, mealthrills_fnb_post.class));
            }
        });

    }
}