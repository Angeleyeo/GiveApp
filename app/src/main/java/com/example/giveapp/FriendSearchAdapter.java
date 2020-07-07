package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ProductViewHolder> {

    private static final String TAG = "FriendSearchAdapter";

    private Context mCtx;
    private List<Users> userList;

    public FriendSearchAdapter(Context mCtx, List<Users> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.user_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Users user = userList.get(position);

        holder.list_fName.setText(user.getName());
        holder.list_email.setText(user.getEmail());

        Picasso.get().load(user.getImageUrl()).into(holder.image_profile);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView list_fName, list_email;
        CircleImageView image_profile;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            list_fName = itemView.findViewById(R.id.list_fName);
            list_email = itemView.findViewById(R.id.list_email);
            image_profile = itemView.findViewById(R.id.image_profile);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Users user = userList.get(getAbsoluteAdapterPosition());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = fAuth.getCurrentUser();

            DocumentReference docRef = db.collection("users").document(currentUser.getUid()).collection("friends").document(user.getId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Intent intent = new Intent(mCtx, Friends_details.class);
                            intent.putExtra("user", user);
                            mCtx.startActivity(intent);

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {

                            Intent intent = new Intent(mCtx, SearchFriends_details.class);
                            intent.putExtra("user", user);
                            mCtx.startActivity(intent);

                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
    }

}

