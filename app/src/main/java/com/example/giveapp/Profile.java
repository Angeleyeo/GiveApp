package com.example.giveapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Profile extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText profileName, profileEmail;
    ImageView profileImageView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button SaveBtn, LogoutBtn;
    FirebaseUser user;
    StorageReference storageReference;
    Uri downloadUri = null;
    String userID;
    ImageButton backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent data = getIntent();
        final String name = data.getStringExtra("Name");
        String email = data.getStringExtra("Email");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
                downloadUri = uri;
            }
        });

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference docRef = fStore.collection("users").document(userID);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Picasso.get().load((String)documentSnapshot.get("imageUrl")).into(profileImageView);

                } else {
                    Log.d("tag", "On Event: Document does not exist.");
                }
            }
        });

        profileName = findViewById(R.id.Username);
        profileEmail = findViewById(R.id.Email);
        profileImageView = findViewById(R.id.imageView);
        SaveBtn = findViewById(R.id.saveBtn);
        LogoutBtn = findViewById(R.id.LogoutBtn);

        profileName.setText(name);
        profileEmail.setText(email);
        Log.d(TAG, "onCreate: " + name + " " + email);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileName.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty() ) {
                    Toast.makeText(Profile.this, "One or Many Fields Are Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = profileEmail.getText().toString();
                final String name = profileName.getText().toString();

                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email", email);
                        edited.put("fName", name);
                        edited.put("imageUrl", downloadUri.toString());
                        edited.put("id", user.getUid()); // added

                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });

                        // search code

                        Client client = new Client("A5FO78D1EG", "006b477cd838e6297ad7a265a88aef92");
                        Index index = client.getIndex("user_search");


                        List<JSONObject> array = new ArrayList<>();

                        Map<String, Object> js = new HashMap<>(edited);
                        js.put("objectID", user.getUid());
                        array.add(new JSONObject(js));


                        index.saveObjectsAsync(new JSONArray(array), null);

                        //

                        Toast.makeText(Profile.this, "User Settings Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        backBtn = findViewById(R.id.prevBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid()+ "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                        downloadUri = uri;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class PerformAction {
        String operationType, userId, profileid;

        public PerformAction(String operationType, String userId, String profileid) {
            this.operationType = operationType;
            this.userId = userId;
            this.profileid = profileid;
        }
    }


}