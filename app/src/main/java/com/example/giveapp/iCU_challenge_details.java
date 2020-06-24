package com.example.giveapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class iCU_challenge_details extends AppCompatActivity {

    private static final String TAG = "iCU_challenge_details";

    TextView challenge_name, longDesc;
    ImageView challenge_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnSend;

    Challenge challenge;

    FirebaseFirestore db;
    CollectionReference challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_challenge_details);

        db = FirebaseFirestore.getInstance();
        challenges = db.collection("challenges");

        btnSend = (FloatingActionButton)findViewById(R.id.donateBtn);

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

}