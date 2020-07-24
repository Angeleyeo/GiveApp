package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class foodDetails extends AppCompatActivity {

    private static final String TAG = "foodDetails";

    TextView foodName, foodDesc, counterTxt;
    Button plusBtn, minusBtn, confirmBtn;
    private int counter;
    ImageView foodLogo;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String ownerID;
    ProgressBar progressBar;

    Food food;

    FirebaseFirestore db;
    CollectionReference foodCollection;
    
    private String mCurrentId;
    private StorageReference mStorage;
    private FirebaseFirestore mff;
    private FirebaseAuth mfa;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        counterTxt = (TextView) findViewById(R.id.counterText);

        db = FirebaseFirestore.getInstance();
        foodCollection = db.collection("food");

        foodDesc = (TextView) findViewById(R.id.foodDesc);
        foodName = (TextView) findViewById(R.id.foodName);
        foodLogo = (ImageView) findViewById(R.id.foodLogo);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        plusBtn = (Button) findViewById(R.id.addQtyBtn);
        minusBtn = (Button) findViewById(R.id.reduceQtyBtn);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);

        //reserve food
        mStorage = FirebaseStorage.getInstance().getReference();
        mff = FirebaseFirestore.getInstance();
        mfa = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar6);


        if (getIntent() != null) {
            food = (Food) getIntent().getSerializableExtra("food");
        }
        assert food != null;
        getDetail(food);
        initCounter();


        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                counterTxt.setText(String.valueOf(counter));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                counterTxt.setText(String.valueOf(counter));
            }
        });

        mCurrentId = FirebaseAuth.getInstance().getUid();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                Map<String, String> userMap = new HashMap<>();
                userMap.put("foodName", food.getFoodName());
                userMap.put("foodQty", food.getFoodQty());
                userMap.put("foodPrice", food.getFoodPrice());
                userMap.put("foodDesc", food.getFoodDesc());
                userMap.put("ownerID", ownerID);

                if (counter <= 0 || counter > Integer.parseInt(food.getFoodQty())) {
                    progressBar.setVisibility(View.GONE);
                    confirmBtn.setError("Quantity only limited to  " + food.getFoodQty());
                    Toast.makeText(foodDetails.this, "Quantity only limited to  " + food.getFoodQty(), Toast.LENGTH_SHORT).show();
                }
                else {
                    mff.collection("users/" + ownerID + "/reservedFood").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(foodDetails.this, "Reserved", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(foodDetails.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    foodCollection.document(food.getId()).delete();

                    String message = "Notification";
                    Map<String, Object> notificationMessage = new HashMap<>();
                    notificationMessage.put("message", message);
                    notificationMessage.put("from", mCurrentId);

                    FirebaseFirestore.getInstance().collection("users/" + ownerID + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(foodDetails.this, "Notification Sent", Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(foodDetails.this, "Error", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

        });
    }



    private void getDetail(final Food food) {
        foodCollection.document(food.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    foodName.setText((String) documentSnapshot.get("foodName"));
                    foodDesc.setText((String) documentSnapshot.get("foodDesc"));
                    ownerID = (String) documentSnapshot.get("ownerID");
                    Picasso.get().load((String) documentSnapshot.get("foodLogo")).into(foodLogo);

                   // SharedPreferences sp = getSharedPreferences("foodIDSP", MODE_PRIVATE);
                    //sp.edit().putString("foodID", documentSnapshot.getId()).apply();

                } else {
                    Log.d(TAG, "Current data: null");
                }
                collapsingToolbarLayout.setTitle(foodName.getText());
            }

        });

    }

    public void initCounter() {
        counter = 0;
        counterTxt.setText(String.valueOf(counter));
    }

}