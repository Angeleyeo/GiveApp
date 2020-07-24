package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class iCU_challenges extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChallengeAdaptor adaptor;
    private List<Challenge> challengeList;
    private ProgressBar progressBar;

    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_challenges);

        challengeList = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_challenges);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new ChallengeAdaptor(this, challengeList);
        recyclerView.setAdapter(adaptor);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("challenges").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Challenge c = d.toObject(Challenge.class);
                                c.setId(d.getId());
                                challengeList.add(c);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iCU_challenges.this, iCU_main.class));
            }
        });
    }

}