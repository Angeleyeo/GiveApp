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

public class rfo_donation_details extends AppCompatActivity {

    private static final String TAG = "rfo_donation_details";

    TextView ben_name, desc;
    ImageView ben_logo;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnDonate;

    Beneficiary beneficiary;

    FirebaseFirestore db;
    CollectionReference beneficiaries;

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfo_donation_details);

        db = FirebaseFirestore.getInstance();
        beneficiaries = db.collection("beneficiaries");

        btnDonate = (FloatingActionButton)findViewById(R.id.donateBtn);

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
        myDialog = new Dialog(rfo_donation_details.this);
        myDialog.setContentView(R.layout.activity_rfo_popup);
        TextView popuptext = myDialog.findViewById(R.id.popuptext);
        final EditText enterAmt = myDialog.findViewById(R.id.enterAmt);
        Button cfmBtn = myDialog.findViewById(R.id.cfmBtn);

        enterAmt.setEnabled(true);
        cfmBtn.setEnabled(true);

        cfmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int amtEntered = Integer.parseInt(enterAmt.getText().toString()); // amount entered
                SharedPreferences sharedPreferences = getSharedPreferences("countSP", MODE_PRIVATE);
                int numSteps = sharedPreferences.getInt("count", 0);
                int amtAvailToBeDonated = numSteps / 10;  // 10 to test, 10000 actual
                int stepsUnavailForConvertion = numSteps % 10; // num steps that cant be converted // 10 to test, 10000 actual

                if (amtEntered > amtAvailToBeDonated) {
                    String errorMsg = "Please enter an amount less than or equal to $" + amtAvailToBeDonated;
                    enterAmt.setError(errorMsg);
                } else if (amtEntered <= 0) {
                    String errorMsg = "Please enter a minimum of $1.";
                    enterAmt.setError(errorMsg);
                } else {
                    int numStepsAftDonation = ((amtAvailToBeDonated - amtEntered) * 10) + stepsUnavailForConvertion; // 10 to test, 10000 actual

                    SharedPreferences pref = getSharedPreferences("countSP", MODE_PRIVATE);
                    pref.edit().putInt("count", numStepsAftDonation).apply();

                    SharedPreferences sp = getSharedPreferences("benIDSP", MODE_PRIVATE);
                    String benID = sp.getString("benID", "default");

                    DocumentReference ben = db.collection("beneficiaries").document(benID);
                    ben
                            .update("userDonation", amtEntered)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });

                    Toast.makeText(rfo_donation_details.this, "Donation Successful", Toast.LENGTH_SHORT).show();

                    myDialog.dismiss();

                    startActivity(new Intent(rfo_donation_details.this, rfo_stepcounter.class));
                    finish();
                }
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