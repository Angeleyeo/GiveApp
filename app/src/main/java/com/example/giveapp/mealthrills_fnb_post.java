package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class mealthrills_fnb_post extends AppCompatActivity {

    private static final String TAG = "mealthrills_fnb_post";

    private ImageView mSelectImage;

    private EditText mItemName;
    private EditText mItemPrice;
    private EditText mItemQty;
    private EditText mItemDesc;

    private Button mListItBtn;

    private Uri postImageUri;

    private StorageReference mStorage;
    private FirebaseFirestore mff;
    private FirebaseAuth mfa;

    private String current_uId;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealthrills_fnb_post);

        mProgressBar = findViewById(R.id.newPostProgressBar);

        mStorage = FirebaseStorage.getInstance().getReference();
        mff = FirebaseFirestore.getInstance();
        mfa = FirebaseAuth.getInstance();

        current_uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mSelectImage = findViewById(R.id.uploadFoodImg);

        mItemName = findViewById(R.id.foodItemName);
        mItemPrice = findViewById(R.id.foodItemPrice);
        mItemQty = findViewById(R.id.foodItemQty);
        mItemDesc = findViewById(R.id.foodItemDesc);
        mListItBtn = findViewById(R.id.listItBtn);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // the following code is written by me to check for permission to read and write storage. ask user "can access media?"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(mealthrills_fnb_post.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(mealthrills_fnb_post.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(mealthrills_fnb_post.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }

                    // code end

                    else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512, 512)
                                .setAspectRatio(1, 1)
                                .start(mealthrills_fnb_post.this);

                    }

                }
            }
        });

        mListItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

            }
        });

    }

    private void startPosting() {

        final String name = mItemName.getText().toString().trim();
        final String price = mItemPrice.getText().toString().trim();
        final String qty = mItemQty.getText().toString().trim();
        final String desc = mItemDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(qty) && postImageUri != null) {

            mProgressBar.setVisibility(View.VISIBLE);

            String randomName = FieldValue.serverTimestamp().toString();

            final StorageReference filePath = mStorage.child("FoodItem_Images").child(randomName + ".jpg");

            filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String imgUrl = uri.toString();

                                SharedPreferences imgUrlSP = getSharedPreferences("imgUrlSP", MODE_PRIVATE);
                                imgUrlSP.edit().putString("imgUrl", imgUrl).apply();

                                Toast.makeText(getBaseContext(), "Upload success", Toast.LENGTH_LONG).show();
                            }
                        });

                        SharedPreferences imgUrlSP = getSharedPreferences("imgUrlSP", MODE_PRIVATE);
                        String imgUrl = imgUrlSP.getString("imgUrl", "default");

                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("image_url", imgUrl);
                        postMap.put("name", name);
                        postMap.put("price", price);
                        postMap.put("qty", qty);
                        postMap.put("desc", desc);
                        postMap.put("user_id", current_uId);
                        postMap.put("timestamp", FieldValue.serverTimestamp());


                        mff.collection("Foods").add(postMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        Toast.makeText(mealthrills_fnb_post.this, "Post was added", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(mealthrills_fnb_post.this, mealthrills_fnbOwners.class));
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mealthrills_fnb_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                mSelectImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}