package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BeneficiaryAdaptor extends RecyclerView.Adapter<BeneficiaryAdaptor.ProductViewHolder> {

    private Context mCtx;
    private List<Beneficiary> beneficiaryList;

    public BeneficiaryAdaptor(Context mCtx, List<Beneficiary> beneficiaryList) {
        this.mCtx = mCtx;
        this.beneficiaryList = beneficiaryList;
    }

    private String theBenLogo;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.beneficiary_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Beneficiary beneficiary = beneficiaryList.get(position);

        holder.textViewBenName.setText(beneficiary.getName());

        Picasso.get().load(beneficiary.getLogo()).into(holder.imageViewBenLogo);
    }

    @Override
    public int getItemCount() {
        return beneficiaryList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewBenName;
        ImageView imageViewBenLogo;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBenName = itemView.findViewById(R.id.textViewBenName);
            imageViewBenLogo = itemView.findViewById(R.id.imageViewBenLogo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Beneficiary beneficiary = beneficiaryList.get(getAbsoluteAdapterPosition());
            Intent intent = new Intent(mCtx, rfo_donation_details.class);
            intent.putExtra("beneficiary", beneficiary);
            mCtx.startActivity(intent);
        }
    }

}