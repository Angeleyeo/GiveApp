package com.example.giveapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class iCU_challenge_details extends AppCompatActivity {

    private static final String TAG = "iCU_challenge_details";

    TextView challenge_name, longDesc;
    ImageView challenge_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnSend;

    Challenge challenge;

    FirebaseFirestore db;
    CollectionReference challenges;

    Dialog myDialog;

    String challengeId;
    String challengeName;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_challenge_details);

        db = FirebaseFirestore.getInstance();
        challenges = db.collection("challenges");

        btnSend = (FloatingActionButton)findViewById(R.id.DoneBtn);

        longDesc = findViewById(R.id.longDesc);
        challenge_name = findViewById(R.id.challenge_name);
        challenge_image = findViewById(R.id.challenge_image);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null) {
            challenge = (Challenge) getIntent().getSerializableExtra("challenge");
        }
        assert challenge != null;

        challengeId = challenge.getId();
        SharedPreferences pref = getSharedPreferences("challengeID", Context.MODE_PRIVATE);
        pref.edit().putString("cID", challengeId).apply();

        challengeName = challenge.getTitle();
        SharedPreferences sPref = getSharedPreferences("challengeName", Context.MODE_PRIVATE);
        pref.edit().putString("cName", challengeName).apply();

        getDetailChallenge(challenge);

    }

    private void getDetailChallenge(final Challenge challenge) {
        challenges.document(challenge.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    challenge_name.setText((String)documentSnapshot.get("title"));
                    longDesc.setText((String)documentSnapshot.get("longDesc"));
                    Picasso.get().load((String)documentSnapshot.get("image")).into(challenge_image);
                } else {
                    Log.d(TAG, "Current data: null");
                }
                collapsingToolbarLayout.setTitle(challenge_name.getText());
            }


        });
    }

    public void showChallengePopUp(View v) {
        myDialog = new Dialog(iCU_challenge_details.this);
        myDialog.setContentView(R.layout.i_c_u_send_challenge_popup);
        TextView sendChallenge = myDialog.findViewById(R.id.sendChallenge);


        final List<Users> friends = new ArrayList<>();

        RecyclerView usersToChallenge = myDialog.findViewById(R.id.usersToChallenge);
        usersToChallenge.setHasFixedSize(true);
        usersToChallenge.setLayoutManager(new LinearLayoutManager(this));

        final SendChallengeAdapter adapter = new SendChallengeAdapter(this, friends);
        usersToChallenge.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(currentUser.getUid()).collection("friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                Users u = d.toObject(Users.class);
                                u.setId(d.getId());

                                if (!u.getId().equals(currentUser.getUid())) {
                                    friends.add(u);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });



        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myDialog.dismiss();
                return true;
            }
        });
        myDialog.show();
    }

}