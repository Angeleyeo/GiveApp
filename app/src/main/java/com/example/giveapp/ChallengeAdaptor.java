package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChallengeAdaptor extends RecyclerView.Adapter<ChallengeAdaptor.ProductViewHolder> {

    private Context mCtx;
    private List<Challenge> challengeList;

    public ChallengeAdaptor(Context mCtx, List<Challenge> challengeList) {
        this.mCtx = mCtx;
        this.challengeList = challengeList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.challenges_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);

        holder.textViewTitle.setText(challenge.getTitle());
        holder.textViewShortDesc.setText(challenge.getShortDesc());
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewShortDesc;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Challenge challenge = challengeList.get(getAbsoluteAdapterPosition());
            Intent intent = new Intent(mCtx, iCU_challenge_details.class);
            intent.putExtra("challenge", challenge);
            mCtx.startActivity(intent);
        }
    }

}
