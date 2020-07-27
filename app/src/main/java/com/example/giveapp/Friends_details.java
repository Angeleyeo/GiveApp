package com.example.giveapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friends_details extends AppCompatActivity {

    private static final String TAG = "Friends_details";

    TextView mFName, mFEmail, mFProfile;
    CircleImageView mFImage;

    Users user;

    FirebaseFirestore db;
    CollectionReference users;

    String otherUsersId; // added
    FirebaseAuth fAuth; // added

    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);

        fAuth = FirebaseAuth.getInstance(); // added

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");

        mFProfile = findViewById(R.id.myFriendsProfile);
        mFName = findViewById(R.id.myFriendsName);
        mFEmail = findViewById(R.id.myFriendsEmail);
        mFImage = findViewById(R.id.myFriendsImage);

        if(getIntent() != null) {
            user = (Users) getIntent().getSerializableExtra("user");
        }
        assert user != null;
        getDetail(user);

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Friends_details.this, SearchFriends.class));
            }
        });
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

                    mFName.setText((String)documentSnapshot.get("fName"));
                    mFEmail.setText((String)documentSnapshot.get("email"));
                    mFProfile.setText((String)documentSnapshot.get("fName"));
                    otherUsersId = (String) documentSnapshot.get("id");

                    Picasso.get().load((String)documentSnapshot.get("imageUrl")).into(mFImage);

                } else {
                    Log.d(TAG, "Current data: null");
                }


            }


        });
    }
}