package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends_details);

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

                    Picasso.get().load((String)documentSnapshot.get("imageUrl")).into(otherUsersImage);

                } else {
                    Log.d(TAG, "Current data: null");
                }


            }


        });
    }

    public void showPopUp(View v) {
        myDialog = new Dialog(SearchFriends_details.this);
        myDialog.setContentView(R.layout.activity_search_friends_popup);
        TextView sendFR = myDialog.findViewById(R.id.sendFR);
        TextView userSent = myDialog.findViewById(R.id.userSent);
        Button sendBtn = myDialog.findViewById(R.id.sendBtn);

        sendBtn.setEnabled(true);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SearchFriends_details.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();

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