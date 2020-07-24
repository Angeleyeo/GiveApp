package com.example.giveapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class confirmCollection extends AppCompatActivity {

    Button confirmBtn;
    Food food;
    ImageButton backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_collection);

        confirmBtn = findViewById(R.id.confirmOrderBtn);

        if (getIntent() != null) {
            food = (Food) getIntent().getSerializableExtra("food");
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete from list
                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("reservedFood")
                        .document(food.getId()).delete();
                startActivity(new Intent(confirmCollection.this, reservedFood.class));

            }
        });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(confirmCollection.this, reservedFood.class));
            }
        });

    }

}
