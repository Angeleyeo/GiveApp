package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<CreditCardDetails> creditCardList;
    private PassChallenges rcvdChallenge;

    public CreditCardAdapter(Context mCtx, List<CreditCardDetails> creditCardList, PassChallenges rcvdChallenge) {
        this.mCtx = mCtx;
        this.creditCardList = creditCardList;
        this.rcvdChallenge = rcvdChallenge;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.credit_card_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CreditCardDetails creditCard = creditCardList.get(position);

        holder.last4digits.setText(creditCard.getLast4());
        holder.list_cardName.setText(creditCard.getName());

        Picasso.get().load(creditCard.getLogoUrl()).into(holder.card_logo);
    }

    @Override
    public int getItemCount() {
        return creditCardList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "ProductViewHolder";

        TextView list_cardNum, last4digits, list_cardName;
        CircleImageView card_logo;
        ImageButton backBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            list_cardNum = itemView.findViewById(R.id.list_cardNum);
            last4digits = itemView.findViewById(R.id.last4digits);
            list_cardName = itemView.findViewById(R.id.list_cardName);
            card_logo = itemView.findViewById(R.id.card_logo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CreditCardDetails creditCard = creditCardList.get(getAbsoluteAdapterPosition());

            final CollectionReference users = FirebaseFirestore.getInstance().collection("users");

            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            final String otherUsersId = rcvdChallenge.getFriendId();

            // friend details

            DocumentReference otherDoc = users.document(otherUsersId);
            otherDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            rcvdChallenge.setStatus("Donated");

                            // in current user's "receivedChallenges", update the challenge status of the challenge submitted

                            String currentUserId = currentUser.getUid();

                            users.document(currentUserId).collection("receivedChallenges").document(rcvdChallenge.getChallengeId()).set(rcvdChallenge);

                            Toast.makeText(mCtx, "Donation Successful!", Toast.LENGTH_LONG).show();

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

            final String currentUserId = currentUser.getUid();

            DocumentReference currentDoc = users.document(currentUserId);
            currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {

                            String challengeId = rcvdChallenge.getChallengeId();
                            String challengeName = rcvdChallenge.getChallengeName();
                            String friendName = (String) document.get("fName");

                            // in other user's "sentChallenges", update the challenge status of the challenge submitted
                            PassChallenges sentChallenge = new PassChallenges(currentUserId, challengeId, friendName, challengeName, "Donated");

                            users.document(otherUsersId).collection("sentChallenges").document(challengeId).set(sentChallenge);

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            Toast.makeText(mCtx, "Challenge Completed.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(mCtx, iCU_main.class); // need to update status
            mCtx.startActivity(i);
        }
    }

}

