package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SentChallengeAdaptor extends RecyclerView.Adapter<SentChallengeAdaptor.ProductViewHolder> {

    private Context mCtx;
    private List<PassChallenges> sentChallengeList;

    public SentChallengeAdaptor (Context mCtx, List<PassChallenges> sentChallengeList) {
        this.mCtx = mCtx;
        this.sentChallengeList = sentChallengeList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.sent_challenges, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        PassChallenges sentChallenge = sentChallengeList.get(position);

        holder.challengeTitle.setText(sentChallenge.getChallengeName());
        holder.challengerName.setText(sentChallenge.getFriendName());
        holder.currentStatus.setText(sentChallenge.getStatus());
    }

    @Override
    public int getItemCount() {
        return sentChallengeList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView challengeTitle, challengerName, sentTo, Status, currentStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            challengeTitle = itemView.findViewById(R.id.challengeTitle);
            challengerName = itemView.findViewById(R.id.challengerName);
            sentTo = itemView.findViewById(R.id.sentTo);
            Status = itemView.findViewById(R.id.Status);
            currentStatus = itemView.findViewById(R.id.currentStatus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) { // when status is pending then user can do sth abt it
            PassChallenges sentChallenge = sentChallengeList.get(getAbsoluteAdapterPosition());
            Intent intent = new Intent();

            if (sentChallenge.getStatus().equals("Pending Approval")) {
                intent = new Intent(mCtx, iCU_sent_details.class);
                intent.putExtra("sentChallenge", sentChallenge);
            } else if (sentChallenge.getStatus().equals("Received")) {
                Toast.makeText(mCtx, "Your friend has received the challenge.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_sent.class);
            } else if (sentChallenge.getStatus().equals("Accepted")) {
                Toast.makeText(mCtx, "Your friend has accepted the challenge.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_sent.class);
            } else if (sentChallenge.getStatus().equals("Rejected")) {
                Toast.makeText(mCtx, "Your friend has rejected the challenge.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_sent.class);
            } else if (sentChallenge.getStatus().equals("Approved")) {
                Toast.makeText(mCtx, "You approved the completion.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_sent.class);
            } else if (sentChallenge.getStatus().equals("Not Approved")) {
                Toast.makeText(mCtx, "You did not approve the completion.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_sent.class);
            }


            mCtx.startActivity(intent);
        }
    }

}