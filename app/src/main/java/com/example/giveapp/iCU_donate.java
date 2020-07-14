package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class iCU_donate extends AppCompatActivity {

    private static final String TAG = "iCU_donate";

    private TextView amtToDonate;

    private RecyclerView recyclerView;
    private BeneficiaryAdapter2 adaptor;
    private List<Beneficiary> beneficiaryList;
    private ProgressBar progressBar;
    ImageButton prevBtn;
    PassChallenges rcvdChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_donate);

        beneficiaryList = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_beneficiaries);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent() != null) {
            rcvdChallenge = (PassChallenges) getIntent().getSerializableExtra("rcvdChallenge");
        }
        assert rcvdChallenge != null;

        adaptor = new BeneficiaryAdapter2(this, beneficiaryList, rcvdChallenge);
        recyclerView.setAdapter(adaptor);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("beneficiaries").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Beneficiary b = d.toObject(Beneficiary.class);
                                b.setId(d.getId());
                                beneficiaryList.add(b);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });

        prevBtn = findViewById(R.id.prevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), iCU_uncompleted.class);
                startActivity(i);
            }
        });
    }


}