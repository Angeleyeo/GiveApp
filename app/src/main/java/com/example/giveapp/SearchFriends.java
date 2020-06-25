package com.example.giveapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchFriends extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView recyclerView;
    private DatabaseReference df;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        //doesnt work(??)
        /**mSearchField = (EditText) findViewById(R.id.Search);
        mSearchBtn = (ImageButton) findViewById(R.id.searchBtn);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        df = FirebaseDatabase.getInstance().getReference("users");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);
            }
        });

    }

    private void firebaseUserSearch(String searchText) {

        Toast.makeText(SearchFriends.this, "Started Search", Toast.LENGTH_SHORT).show();

        Query firebaseSearchQuery = df.orderByChild("fName").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(df, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

                holder.setDetails(getApplicationContext(), model.getName(), model.getEmail(), model.getImageUrl());

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    //View Holder class
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        StorageReference storageReference;
        FirebaseAuth fAuth;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            fAuth = FirebaseAuth.getInstance();
        }

        public void setDetails(Context ctx, String Name, String Email, String userImage) {
            TextView name = (TextView) mView.findViewById(R.id.Name);
            TextView email = (TextView) mView.findViewById(R.id.email);

            name.setText(Name);
            email.setText(Email);

            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            //profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                //@Override
                //public void onSuccess(Uri uri) {
                    //Picasso.get().load(uri).into((ImageView) mView.findViewById(R.id.profilePic));
                //}
           // });
        }
    }**/
    }
}