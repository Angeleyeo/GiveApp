package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Friends extends AppCompatActivity {

    private static final String TAG = "Friends";

    private RecyclerView recyclerView;
    private FriendsAdapter adaptor;
    private List<Users> friendList;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user = fAuth.getCurrentUser();

        friendList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.myFriendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new FriendsAdapter(this, friendList);
        recyclerView.setAdapter(adaptor);


        db.collection("users").document(user.getUid()).collection("friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for (DocumentSnapshot d : list) {
                                Users u = d.toObject(Users.class);
                                u.setId(d.getId());
                                friendList.add(u);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });
    }
}