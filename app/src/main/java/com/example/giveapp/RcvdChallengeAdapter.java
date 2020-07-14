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

public class RcvdChallengeAdapter extends RecyclerView.Adapter<RcvdChallengeAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<PassChallenges> rcvdChallengeList;

    public RcvdChallengeAdapter (Context mCtx, List<PassChallenges> rcvdChallengeList) {
        this.mCtx = mCtx;
        this.rcvdChallengeList = rcvdChallengeList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.rcvd_challenges, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        PassChallenges rcvdChallenge = rcvdChallengeList.get(position);

        holder.challengeTitle.setText(rcvdChallenge.getChallengeName());
        holder.challengerName.setText(rcvdChallenge.getFriendName());
        holder.currentStatus.setText(rcvdChallenge.getStatus());
    }

    @Override
    public int getItemCount() {
        return rcvdChallengeList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView challengeTitle, challengerName, sentBy, Status, currentStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            challengeTitle = itemView.findViewById(R.id.challengeTitle);
            challengerName = itemView.findViewById(R.id.challengerName);
            sentBy = itemView.findViewById(R.id.sentBy);
            Status = itemView.findViewById(R.id.Status);
            currentStatus = itemView.findViewById(R.id.currentStatus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PassChallenges rcvdChallenge = rcvdChallengeList.get(getAbsoluteAdapterPosition());
            Intent intent = new Intent();

            if (rcvdChallenge.getStatus().equals("Received")) {
                intent = new Intent(mCtx, iCU_uncompleted_accept_reject.class);
            } else if (rcvdChallenge.getStatus().equals("Accepted")) {
                intent = new Intent(mCtx, iCU_uncompleted_details.class);
            } else if (rcvdChallenge.getStatus().equals("Pending Approval")) {
                Toast.makeText(mCtx, "Your challenge completion is pending approval.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_uncompleted.class);
            } else if (rcvdChallenge.getStatus().equals("Approved")) {
                Toast.makeText(mCtx, "Challenge Completion Approved! You have completed challenge successfully.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_uncompleted.class);
            } else if (rcvdChallenge.getStatus().equals("Rejected")) {
                Toast.makeText(mCtx, "Please donate $0.10 to any beneficiary of choice.", Toast.LENGTH_LONG).show();
                intent = new Intent(mCtx, iCU_donate.class);
            } else if (rcvdChallenge.getStatus().equals("Not Approved")) {
            Toast.makeText(mCtx, "Your challenge completion was not approved, please donate $0.10 to any beneficiary of choice.", Toast.LENGTH_LONG).show();
            intent = new Intent(mCtx, iCU_donate.class);
        }
            intent.putExtra("rcvdChallenge", rcvdChallenge);
            mCtx.startActivity(intent);

        }
    }

}