package com.example.giveapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class iCU_main extends AppCompatActivity {

    Button mUncompleted, mFriends, mSent;
    ImageButton backBtn;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_main);


  /*      Challenge man = new Challenge("BOTTLE CAP CHALLENGE 2", "byebye mozzies", "", "");
        Challenge bfc = new Challenge("BARE FACED CHALLENGE", "flaunt that no-makeup beauty!", "", "");
        Challenge mov = new Challenge("MOVEMBER", "mucho mustache ~", "", "");
        Challenge iott = new Challenge("#itsoktotalk", "it's okay.", "", "");

        db.collection("challenges").add(iott);
        db.collection("challenges").add(dsc);
        db.collection("challenges").add(bfc);
      db.collection("challenges").add(man);
 */


        mUncompleted = findViewById(R.id.uncompletedchallenges);
        mFriends = findViewById(R.id.challengefriends);
        mSent = findViewById(R.id.sentchallenges);

        mFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iCU_main.this, iCU_challenges.class));
            }
        });

        mUncompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iCU_main.this, iCU_uncompleted.class));
            }
        });

        mSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iCU_main.this, iCU_sent.class));
            }
        });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(iCU_main.this, MainActivity.class));
            }
        });

    }

}