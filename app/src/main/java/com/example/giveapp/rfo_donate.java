package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class rfo_donate extends AppCompatActivity {

    private TextView amtToDonate;

    private RecyclerView recyclerView;
    private BeneficiaryAdaptor adaptor;
    private List<Beneficiary> beneficiaryList;
    private ProgressBar progressBar;
    ImageButton prevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfo_donate);


        amtToDonate = findViewById(R.id.money);
        SharedPreferences sharedPreferences = getSharedPreferences("countSP", MODE_PRIVATE);
        int numSteps = sharedPreferences.getInt("count", 0);
        int amt = numSteps/10;  // 10 to test, 10000 actual
        amtToDonate.setText(Integer.toString(amt));

        beneficiaryList = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_beneficiaries);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new BeneficiaryAdaptor(this, beneficiaryList);
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
                Intent i = new Intent(v.getContext(), rfo_stepcounter.class);
                startActivity(i);
            }
        });
    }


}