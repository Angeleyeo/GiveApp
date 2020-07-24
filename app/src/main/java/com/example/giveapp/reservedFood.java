package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class reservedFood extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<Food> foodReservedList;
    private reservedFoodAdapter reservedFoodAdapter;
    ProgressBar progressBar;
    ImageButton prevButton;

    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_food);

        recyclerView = findViewById(R.id.recyclerView_reservedFood);
        foodReservedList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar5);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        reservedFoodAdapter = new reservedFoodAdapter(this, foodReservedList);
        recyclerView.setAdapter(reservedFoodAdapter);

        prevButton = findViewById(R.id.prevBtn);

        String ownerID = FirebaseAuth.getInstance().getUid();
        db.collection("users").document(ownerID).collection("reservedFood").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Food f = d.toObject(Food.class);
                                f.setId(d.getId());
                                foodReservedList.add(f);

                            }
                            reservedFoodAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(reservedFood.this, "Error", Toast.LENGTH_SHORT).show();            }

        });

        prevButton = findViewById(R.id.prevBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), mealthrills_fnbOwners.class);
                startActivity(i);
            }
        });

    }
}