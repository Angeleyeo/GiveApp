package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class userFoodMain extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<Food> foodList;
    private FoodAdaptor foodAdaptor;
    ProgressBar progressBar;
    ImageButton prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_food_main);

        recyclerView = findViewById(R.id.recyclerView_food);
        foodList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar4);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        foodAdaptor = new FoodAdaptor(this, foodList);
        recyclerView.setAdapter(foodAdaptor);

        prevButton = findViewById(R.id.prevBtn);

        db.collection("food").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Food f = d.toObject(Food.class);
                                f.setId(d.getId());
                                foodList.add(f);

                            }
                            foodAdaptor.notifyDataSetChanged();
                        }
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