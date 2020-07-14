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

public class SearchFriends_details extends AppCompatActivity {

    private static final String TAG = "SearchFriends_details";

    TextView otherUsersName, otherUsersEmail, otherUsersProfile;
    CircleImageView otherUsersImage;
    Button addFriendBtn;

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
        setContentView(R.layout.activity_search_friends_details);

        fAuth = FirebaseAuth.getInstance(); // added

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        addFriendBtn = findViewById(R.id.addFriendBtn);

        otherUsersProfile = findViewById(R.id.otherUsersProfile);
        otherUsersName = findViewById(R.id.otherUsersName);
        otherUsersEmail = findViewById(R.id.otherUsersEmail);
        otherUsersImage = findViewById(R.id.otherUsersImage);

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
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) { // no id field

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());

                    otherUsersName.setText((String)documentSnapshot.get("fName"));
                    otherUsersEmail.setText((String)documentSnapshot.get("email"));
                    otherUsersProfile.setText((String)documentSnapshot.get("fName"));
                    otherUsersId = (String) documentSnapshot.get("id");

                    Picasso.get().load((String)documentSnapshot.get("imageUrl")).into(otherUsersImage);

                } else {
                    Log.d(TAG, "Current data: null");
                }


            }


        });
    }

    public void showPopUp(View v) {
        myDialog = new Dialog(SearchFriends_details.this);
        myDialog.setContentView(R.layout.search_friends_popup);
        TextView sendFR = myDialog.findViewById(R.id.sendFR);
        TextView userSent = myDialog.findViewById(R.id.userSent);
        Button sendBtn = myDialog.findViewById(R.id.sendBtn);
        userSent.setText(otherUsersName.getText());

        sendBtn.setEnabled(true);

        sendBtn.setOnClickListener(new View.OnClickListener() {
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

                                String friendName = (String) document.get("fName");
                                String friendEmail = (String) document.get("email");
                                String friendPic = (String) document.get("imageUrl");

                                Users friend = new Users(friendName, friendEmail, friendPic, otherUsersId);

                                // in current user's "sent requests", add the user details of friend

                                users.document(currentUserId).collection("sentRequests").document(otherUsersId).set(friend);

                                Toast.makeText(SearchFriends_details.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();

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

                                // in other user's "received requests", add the user details of current user

                                users.document(otherUsersId).collection("receivedRequests").document(currentUserId).set(me);

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

                startActivity(new Intent(SearchFriends_details.this, MainActivity.class));
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