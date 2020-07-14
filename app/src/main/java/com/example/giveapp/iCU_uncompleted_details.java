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

public class iCU_uncompleted_details extends AppCompatActivity {

    private static final String TAG = "iCU_uncompleted_details";

    TextView challenge_name, longDesc;
    ImageView challenge_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnDone;

    PassChallenges rcvdChallenge;

    FirebaseFirestore db;
    CollectionReference challenges;
    CollectionReference users;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    Dialog myDialog;

    String userToApproveName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_uncompleted_details);

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
            rcvdChallenge = (PassChallenges) getIntent().getSerializableExtra("rcvdChallenge");
        }
        assert rcvdChallenge != null;

        getDetailChallenge(rcvdChallenge);

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

    public void showDonePopUp(View v) {
        myDialog = new Dialog(iCU_uncompleted_details.this);
        myDialog.setContentView(R.layout.i_c_u_uncompleted_send_fa_popup);
        TextView sendForApproval = myDialog.findViewById(R.id.sendForApproval);
        TextView userToApprove = myDialog.findViewById(R.id.userToApprove);
        Button sendFABtn = myDialog.findViewById(R.id.sendFABtn);
        userToApprove.setText(userToApproveName);

        sendFABtn.setEnabled(true);

        sendFABtn.setOnClickListener(new View.OnClickListener() {  // admin!! so there will be collection (friends, rvd, sent all need) for new users le
            @Override
            public void onClick(View v) {

                final String otherUsersId = rcvdChallenge.getFriendId();


                // friend details

                DocumentReference otherDoc = users.document(otherUsersId);
                otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                rcvdChallenge.setStatus("Pending Approval");

                                // in current user's "receivedChallenges", update the challenge status of the challenge submitted

                                String currentUserId = currentUser.getUid();

                                users.document(currentUserId).collection("receivedChallenges").document(rcvdChallenge.getChallengeId()).set(rcvdChallenge);

                                Toast.makeText(iCU_uncompleted_details.this, "Completion of Challenge Sent For Approval", Toast.LENGTH_LONG).show();

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                // current user details

                final String currentUserId = currentUser.getUid();

                DocumentReference currentDoc = users.document(currentUserId);
                currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                String challengeId = rcvdChallenge.getChallengeId();
                                String challengeName = rcvdChallenge.getChallengeName();
                                String friendName = (String) document.get("fName");

                                // in other user's "sentChallenges", update the challenge status of the challenge submitted
                                PassChallenges sentChallenge = new PassChallenges(currentUserId, challengeId, friendName, challengeName, "Pending Approval");

                                users.document(otherUsersId).collection("sentChallenges").document(challengeId).set(sentChallenge);

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

                startActivity(new Intent(iCU_uncompleted_details.this, iCU_uncompleted.class));
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