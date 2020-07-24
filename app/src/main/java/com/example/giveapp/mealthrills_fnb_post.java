package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class mealthrills_fnb_post extends AppCompatActivity {

    private static final String TAG = "mealthrills_fnb_post";

    private ImageView mSelectImage;

    private EditText mItemName;
    private EditText mItemPrice;
    private EditText mItemQty;
    private EditText mItemDesc;

    private Button mListItBtn;
    ProgressBar progressBar;

    private StorageReference mStorage;
    private FirebaseFirestore mff;
    private FirebaseAuth mfa;

    private String current_uId;
    ImageButton prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealthrills_fnb_post);

        progressBar = findViewById(R.id.progressBar3);

        mStorage = FirebaseStorage.getInstance().getReference();
        mff = FirebaseFirestore.getInstance();
        mfa = FirebaseAuth.getInstance();

        current_uId = mfa.getCurrentUser().getUid();

        mSelectImage = findViewById(R.id.uploadFoodImg);

        mItemName = findViewById(R.id.foodItemName);
        mItemPrice = findViewById(R.id.foodItemPrice);
        mItemQty = findViewById(R.id.foodItemQty);
        mItemDesc = findViewById(R.id.foodItemDesc);
        mListItBtn = findViewById(R.id.listItBtn);


        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        prevButton = findViewById(R.id.prevBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), mealthrills_fnbOwners.class);
                startActivity(i);
            }
        });


        mListItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = mItemName.getText().toString().trim();
                final String qty = mItemQty.getText().toString().trim();
                final String price = mItemPrice.getText().toString().trim();
                final String desc = mItemDesc.getText().toString().trim();
                FirebaseUser owner = FirebaseAuth.getInstance().getCurrentUser();
                final String ownerID = owner.getUid();


                if (TextUtils.isEmpty(name)) {
                    mItemName.setError("Name is required.");
                    return;
                }
                if (TextUtils.isEmpty(qty)) {
                    mItemQty.setError("Quantity is required.");
                    return;
                }
                if (TextUtils.isEmpty(price)) {
                    mItemPrice.setError("Price is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                Map<String, String> userMap = new HashMap<>();
                userMap.put("foodName", name);
                userMap.put("foodQty", qty);
                userMap.put("foodPrice", price);
                userMap.put("foodDesc", desc);
                userMap.put("ownerID", ownerID);

                mff.collection("food").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(mealthrills_fnb_post.this, "Food Added", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mealthrills_fnb_post.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                mff.collection("food")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Log.d(TAG, doc.getId() + " => " + doc.getData());
                                    }
                                }
                                else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                        });
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = mStorage.child("food/" + current_uId + "/foodLogo.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mSelectImage);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mealthrills_fnb_post.this, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}