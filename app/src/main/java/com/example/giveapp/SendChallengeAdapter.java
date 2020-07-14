package com.example.giveapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendChallengeAdapter extends RecyclerView.Adapter<SendChallengeAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Users> userList;

    public SendChallengeAdapter(Context mCtx, List<Users> userList) {
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
        private static final String TAG = "ProductViewHolder";

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

            SharedPreferences pref = mCtx.getSharedPreferences("challengeID", Context.MODE_PRIVATE);
            final String challengeId = pref.getString("cID", "default");

            SharedPreferences sPref = mCtx.getSharedPreferences("challengeName", Context.MODE_PRIVATE);
            final String challengeName = pref.getString("cName", "default");

            final Users user = userList.get(getAbsoluteAdapterPosition());

            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            final CollectionReference users = FirebaseFirestore.getInstance().collection("users");


            DocumentReference check = users.document(currentUser.getUid()).collection("sentChallenges").document(challengeId);

            check.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Toast.makeText(mCtx, "This challenge has already been sent to " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            return;

                        } else {

                            // friend details

                            DocumentReference otherDoc = users.document(user.getId());
                            otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {

                                            // in current user's "sent challenges", add the challenge details of challenge sent

                                            PassChallenges challengeSent = new PassChallenges(user.getId(), challengeId, user.getName(), challengeName, "Uncompleted");

                                            users.document(currentUser.getUid()).collection("sentChallenges").document(challengeId).set(challengeSent);

                                            Toast.makeText(mCtx, "Challenge Request Sent To: " + user.getName() , Toast.LENGTH_SHORT).show();

                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                            // current user details

                            DocumentReference currentDoc = users.document(currentUser.getUid());
                            currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {

                                            // in other user's "received challenges", add the challenge details of challenge rcvd

                                            String cUserName = (String) document.get("fName");

                                            PassChallenges challengeRcvd = new PassChallenges(currentUser.getUid(), challengeId, cUserName, challengeName, "Received");

                                            users.document(user.getId()).collection("receivedChallenges").document(challengeId).set(challengeRcvd);

                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });


        }
    }

}

