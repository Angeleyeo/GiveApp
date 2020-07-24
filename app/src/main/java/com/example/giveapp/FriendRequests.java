package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendRequests extends AppCompatActivity {

    private static final String TAG = "FriendRequests";

    private RecyclerView recyclerView;
    private FriendRequestAdapter adaptor;
    private ArrayList<Users> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    ImageButton backBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user = fAuth.getCurrentUser();

        requestList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.friendReqRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new FriendRequestAdapter(this, requestList);
        recyclerView.setAdapter(adaptor);


        db.collection("users").document(user.getUid()).collection("receivedRequests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (DocumentSnapshot d : list) {
                                Users u = d.toObject(Users.class);
                                u.setId(d.getId());
                                requestList.add(u);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendRequests.this, MainActivity.class));
            }
        });
    }
}