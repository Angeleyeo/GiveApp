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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequests_details extends AppCompatActivity {

    private static final String TAG = "FriendRequests_details";

    TextView reqName, reqEmail, reqProfile;
    CircleImageView reqImage;
    Button acceptFriendBtn, rejectFriendBtn;

    Users user;

    FirebaseFirestore db;
    CollectionReference users;

    Dialog myDialog;

    String otherUsersId; // added
    FirebaseAuth fAuth; // added
    String currentUserId; //added


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests_details);

        fAuth = FirebaseAuth.getInstance(); // added

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        acceptFriendBtn = findViewById(R.id.acceptFriendBtn);
        rejectFriendBtn = findViewById(R.id.rejFriendBtn);


        reqProfile = findViewById(R.id.reqProfile);
        reqName = findViewById(R.id.reqName);
        reqEmail = findViewById(R.id.reqEmail);
        reqImage = findViewById(R.id.reqImage);

        if(getIntent() != null) {
            user = (Users) getIntent().getSerializableExtra("user");
        }
        assert user != null;
        getDetail(user);

    }

    // need to clean up - show users excluding current users

    private void getDetail(final Users user) {
        users.document(user.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) { // document id

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    reqName.setText((String)documentSnapshot.get("fName"));
                    reqEmail.setText((String)documentSnapshot.get("email"));
                    reqProfile.setText((String)documentSnapshot.get("fName"));
                    otherUsersId = (String) documentSnapshot.get("id");

                    Picasso.get().load((String)documentSnapshot.get("imageUrl")).into(reqImage);

                } else {
                    Log.d(TAG, "Current data: null");
                }


            }


        });
    }

    public void showAcceptPopUp(View v) {
        myDialog = new Dialog(FriendRequests_details.this);
        myDialog.setContentView(R.layout.activity_friend_requests_acceptpopup);
        TextView acceptFR = myDialog.findViewById(R.id.acceptFR);
        TextView userWhoSent = myDialog.findViewById(R.id.userWhoSent);
        Button acceptBtn = myDialog.findViewById(R.id.acceptBtn);
        userWhoSent.setText(reqName.getText());

        acceptBtn.setEnabled(true);

        acceptBtn.setOnClickListener(new View.OnClickListener() {  // admin!! so there will be collection (friends, rvd, sent all need) for new users le
            @Override
            public void onClick(View v) {

                // friend details

                DocumentReference otherDoc = users.document(otherUsersId);
                otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                final String friendName = (String) document.get("fName");
                                String friendEmail = (String) document.get("email");
                                String friendPic = (String) document.get("imageUrl");

                                Users friend = new Users(friendName, friendEmail, friendPic, otherUsersId);

                                // in current user's "friends", add the user details of friend

                                users.document(currentUserId).collection("friends").document(otherUsersId).set(friend);

                                Toast.makeText(FriendRequests_details.this, "You are now friends with " + friendName, Toast.LENGTH_SHORT).show();

                                // in current user's "rcvd requests", dlt the user details of friend

                                users.document(currentUserId).collection("receivedRequests").document(otherUsersId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

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

                currentUserId = fAuth.getCurrentUser().getUid();

                DocumentReference currentDoc = users.document(currentUserId);
                currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                String myName = (String) document.get("fName");
                                String myEmail = (String) document.get("email");
                                String myPic = (String) document.get("imageUrl");

                                Users me = new Users(myName, myEmail, myPic, currentUserId);

                                // in other user's "friends", add the user details of current user

                                users.document(otherUsersId).collection("friends").document(currentUserId).set(me);

                                // in other user's "sent requests", dlt the user details of current user

                                users.document(otherUsersId).collection("sentRequests").document(currentUserId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

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

                startActivity(new Intent(FriendRequests_details.this, MainActivity.class));
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

    public void showRejectPopUp(View v) {
        myDialog = new Dialog(FriendRequests_details.this);
        myDialog.setContentView(R.layout.activity_friend_requests_rejectpopup);
        TextView rejectFR = myDialog.findViewById(R.id.rejFR);
        TextView userWhoSent = myDialog.findViewById(R.id.userWhoSent);
        Button rejBtn = myDialog.findViewById(R.id.rejBtn);
        userWhoSent.setText(reqName.getText());

        rejBtn.setEnabled(true);

        rejBtn.setOnClickListener(new View.OnClickListener() {  // admin!! so there will be collection (friends, rvd, sent all need) for new users le
            @Override
            public void onClick(View v) { 

                // friend details

                DocumentReference otherDoc = users.document(otherUsersId);
                otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                final String friendName = (String) document.get("fName");
                                String friendEmail = (String) document.get("email");
                                String friendPic = (String) document.get("imageUrl");

                                Users friend = new Users(friendName, friendEmail, friendPic, otherUsersId);

                                // in current user's "rcvd requests", dlt the user details of friend

                                users.document(currentUserId).collection("receivedRequests").document(otherUsersId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

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

                currentUserId = fAuth.getCurrentUser().getUid();

                DocumentReference currentDoc = users.document(currentUserId);
                currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {

                                String myName = (String) document.get("fName");
                                String myEmail = (String) document.get("email");
                                String myPic = (String) document.get("imageUrl");

                                Users me = new Users(myName, myEmail, myPic, currentUserId);

                                // in other user's "sent requests", dlt the user details of current user

                                users.document(otherUsersId).collection("sentRequests").document(currentUserId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });

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

                startActivity(new Intent(FriendRequests_details.this, MainActivity.class));
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