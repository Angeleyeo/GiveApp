package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// should have home btn to iCU_main.

public class iCU_uncompleted extends AppCompatActivity {

    private static final String TAG = "iCU_uncompleted";

    private RecyclerView recyclerView;
    private RcvdChallengeAdapter adaptor;
    private List<PassChallenges> rChallengeList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_uncompleted);

        rChallengeList = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        recyclerView = (RecyclerView) findViewById(R.id.rcvdChallengesRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new RcvdChallengeAdapter(this, rChallengeList);
        recyclerView.setAdapter(adaptor);


        db.collection("users").document(user.getUid()).collection("receivedChallenges").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (DocumentSnapshot d : list) {
                                PassChallenges pc = d.toObject(PassChallenges.class);
                                pc.setChallengeId(d.getId());
                                rChallengeList.add(pc);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });
    }

}