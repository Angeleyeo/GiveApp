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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class iCU_donation_details extends AppCompatActivity {

    private static final String TAG = "iCU_donation_details";

    TextView ben_name, desc;
    ImageView ben_logo;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnDonate;

    Beneficiary beneficiary;

    FirebaseFirestore db;
    CollectionReference beneficiaries;

    Dialog myDialog;

    private PassChallenges rcvdChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_c_u_donation_details);

        db = FirebaseFirestore.getInstance();
        beneficiaries = db.collection("beneficiaries");

        btnDonate = (FloatingActionButton)findViewById(R.id.DoneBtn);

        desc = findViewById(R.id.desc);
        ben_name = findViewById(R.id.ben_name);
        ben_logo = findViewById(R.id.ben_logo);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null) {
            beneficiary = (Beneficiary) getIntent().getSerializableExtra("beneficiary");
        }
        assert beneficiary != null;
        getDetail(beneficiary);

        if(getIntent() != null) {
            rcvdChallenge = (PassChallenges) getIntent().getSerializableExtra("rcvdChallenge");
        }
        assert rcvdChallenge != null;

    }

    private void getDetail(final Beneficiary beneficiary) {
        beneficiaries.document(beneficiary.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    ben_name.setText((String)documentSnapshot.get("name"));
                    desc.setText((String)documentSnapshot.get("desc"));
                    Picasso.get().load((String)documentSnapshot.get("logo")).into(ben_logo);

                    SharedPreferences sp = getSharedPreferences("benIDSP", MODE_PRIVATE);
                    sp.edit().putString("benID", documentSnapshot.getId()).apply();

                } else {
                    Log.d(TAG, "Current data: null");
                }
                collapsingToolbarLayout.setTitle(ben_name.getText());

            }


        });
    }

    public void showPopUp(View v) {
        myDialog = new Dialog(iCU_donation_details.this);
        myDialog.setContentView(R.layout.icu_popup);
        TextView popuptext = myDialog.findViewById(R.id.popuptext);
        TextView tenCents = myDialog.findViewById(R.id.cfmText);
        Button cfmBtn = myDialog.findViewById(R.id.cfmBtn);

        cfmBtn.setEnabled(true);

        cfmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(iCU_donation_details.this, CreditCardList.class);
                i.putExtra("rcvdChallenge", rcvdChallenge);
                startActivity(i);
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