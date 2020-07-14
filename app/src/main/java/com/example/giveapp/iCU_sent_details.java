package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class iCU_sent_details extends AppCompatActivity {

    private static final String TAG = "iCU_sent_details";

    TextView challenge_name, longDesc;
    ImageView challenge_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnDone;

    PassChallenges sentChallenge;

    FirebaseFirestore db;
    CollectionReference challenges;
    CollectionReference users;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Dialog myDialog;

    String userToApproveName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_sent_details);

        db = FirebaseFirestore.getInstance();
        challenges = db.collection("challenges");
        users = db.collection("users");

        btnDone = (FloatingActionButton)findViewById(R.id.DoneBtn);

        longDesc = findViewById(R.id.longDesc);
        challenge_name = findViewById(R.id.challenge_name);
        challenge_image = findViewById(R.id.challenge_image);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null) {
            sentChallenge = (PassChallenges) getIntent().getSerializableExtra("sentChallenge");
        }
        assert sentChallenge != null;

        getDetailChallenge(sentChallenge);

    }

    private void getDetailChallenge(final PassChallenges rcvdChallenge) {

        String challengeId = rcvdChallenge.getChallengeId();
        userToApproveName = rcvdChallenge.getFriendName();

        challenges.document(challengeId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

    public void showToApprovePopUp(View v) {
        myDialog = new Dialog(iCU_sent_details.this);
        myDialog.setContentView(R.layout.icu_sent_detail_popup);

        Button approveBtn = myDialog.findViewById(R.id.approveBtn);
        Button doNotApproveBtn = myDialog.findViewById(R.id.doNotApproveBtn);

        approveBtn.setEnabled(true);

        approveBtn.setOnClickListener(new View.OnClickListener() {  // admin!! so there will be collection (friends, rvd, sent all need) for new users le
            @Override
            public void onClick(View v) {

                final String currentUserId = currentUser.getUid();
                final String otherUsersId = sentChallenge.getFriendId();

                DocumentReference currentDoc = users.document(currentUserId);
                currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {


                                String challengeId = sentChallenge.getChallengeId();
                                String challengeName = sentChallenge.getChallengeName();
                                String friendName = (String) document.get("fName");

                                // in other user's "rcvdChallenges", update the challenge status of the challenge submitted
                                PassChallenges rcvdChallenge = new PassChallenges(currentUserId, challengeId, friendName, challengeName, "Approved");

                                users.document(otherUsersId).collection("receivedChallenges").document(challengeId).set(rcvdChallenge);

                                Toast.makeText(iCU_sent_details.this, "Challenge Approved", Toast.LENGTH_LONG).show();

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                DocumentReference otherDoc = users.document(otherUsersId);
                otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                // in current user's "sentChallenges", update the challenge status of the challenge submitted
                                sentChallenge.setStatus("Approved");

                                users.document(currentUserId).collection("sentChallenges").document(sentChallenge.getChallengeId()).set(sentChallenge);

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                myDialog.dismiss();

                Intent i = new Intent(iCU_sent_details.this, iCU_main.class);
                i.putExtra("approvedChallenge", sentChallenge);
                startActivity(i);
                finish();

            }
        });

        doNotApproveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUserId = currentUser.getUid();
                final String otherUsersId = sentChallenge.getFriendId();

                DocumentReference currentDoc = users.document(currentUserId);
                currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {


                                String challengeId = sentChallenge.getChallengeId();
                                String challengeName = sentChallenge.getChallengeName();
                                String friendName = (String) document.get("fName");

                                // in other user's "rcvdChallenges", update the challenge status of the challenge submitted
                                PassChallenges rcvdChallenge = new PassChallenges(currentUserId, challengeId, friendName, challengeName, "Not Approved");

                                users.document(otherUsersId).collection("receivedChallenges").document(challengeId).set(rcvdChallenge);

                                Toast.makeText(iCU_sent_details.this, "Challenge Rejected", Toast.LENGTH_LONG).show();

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                DocumentReference otherDoc = users.document(otherUsersId);
                otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                // in current user's "sentChallenges", update the challenge status of the challenge submitted
                                sentChallenge.setStatus("Not Approved");

                                users.document(currentUserId).collection("sentChallenges").document(sentChallenge.getChallengeId()).set(sentChallenge);

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                myDialog.dismiss();

                Intent i = new Intent(iCU_sent_details.this, iCU_sent.class);
                i.putExtra("approvedChallenge", sentChallenge);
                startActivity(i);
                finish();

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