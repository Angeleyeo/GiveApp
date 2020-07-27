package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreditCardList extends AppCompatActivity {

    private static final String TAG = "CreditCardList";

    Button addCardBtn;
    private RecyclerView recyclerView;
    private CreditCardAdapter adaptor;
    private List<CreditCardDetails> creditCardList;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    PassChallenges rcvdChallenge;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_list);

        addCardBtn = findViewById(R.id.addCardBtn);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user = fAuth.getCurrentUser();

        creditCardList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.cardRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent() != null) {
            rcvdChallenge = (PassChallenges) getIntent().getSerializableExtra("rcvdChallenge");
        }
        assert rcvdChallenge != null;

        adaptor = new CreditCardAdapter(this, creditCardList, rcvdChallenge);
        recyclerView.setAdapter(adaptor);


        db.collection("users").document(user.getUid()).collection("creditCards").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (DocumentSnapshot d : list) {
                                CreditCardDetails c = d.toObject(CreditCardDetails.class);
                                c.setId(d.getId());
                                creditCardList.add(c);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });


        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreditCardList.this, CreditCard.class);
                startActivity(i);
            }
        });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreditCardList.this, iCU_uncompleted_details.class));
            }
        });
    }

}