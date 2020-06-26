package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    TextView fullName, email, verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button verifyBtn, icuBtn, rfoBtn, mtBtn;
    FirebaseUser user;
    ImageView profileImage, editProfileBtn, findFriends;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        icuBtn = findViewById(R.id.icuBtn);
        rfoBtn = findViewById(R.id.rfoBtn);
        mtBtn = findViewById(R.id.mealThrillsBtn);
        email = findViewById(R.id.emaillAdd);
        fullName = findViewById(R.id.ProfileName);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyMsg = findViewById(R.id.verifyMsg);
        profileImage = findViewById(R.id.profilePic);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        findFriends = findViewById(R.id.findFriendsIV);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //reminder to verify email
        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);

            verifyMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "on Failure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }


        // set name and email in profile
        DocumentReference docRef = fStore.collection("users").document(userID);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    email.setText(documentSnapshot.getString("email"));
                    fullName.setText(documentSnapshot.getString("fName"));
                } else {
                    Log.d("tag", "On Event: Document does not exist.");
                }
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Profile.class);
                i.putExtra("Name", fullName.getText().toString());
                i.putExtra("Email", email.getText().toString());
                startActivity(i);
            }
        });


        icuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), iCU_main.class);
                startActivity(i);
            }
        });

        rfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(v.getContext(), rfo_stepcounter.class);
                //startActivity(i);
            }
        });

        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchFriends.class));
            }
        });

    }


}