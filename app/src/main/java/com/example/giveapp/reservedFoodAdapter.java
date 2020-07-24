package com.example.giveapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class reservedFoodAdapter extends RecyclerView.Adapter<reservedFoodAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Food> reservedFoodList;
    Dialog myDialog;


    public reservedFoodAdapter(Context mCtx, List<Food> reservedFoodList) {
        this.mCtx = mCtx;
        this.reservedFoodList = reservedFoodList;
    }

    @NonNull
    @Override
    public reservedFoodAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new reservedFoodAdapter.ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.reserved_food_view, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull reservedFoodAdapter.ProductViewHolder holder, int position) {
        Food food = reservedFoodList.get(position);

        holder.foodName.setText(food.getFoodName());
        holder.foodPrice.setText("S$" + food.getFoodPrice());
        holder.foodQty.setText(food.getFoodQty());

        Picasso.get().load(food.getFoodLogo()).into(holder.foodLogo);

    }

    @Override
    public int getItemCount() {
        return reservedFoodList.size();    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView foodName, foodPrice, foodQty;
        ImageView foodLogo;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.foodName);
            foodQty = itemView.findViewById(R.id.foodQty);
            foodLogo = itemView.findViewById(R.id.foodLogo);
            foodPrice = itemView.findViewById(R.id.foodPrice);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Food food = reservedFoodList.get(getAbsoluteAdapterPosition());
            Intent intent;
            intent = new Intent(mCtx, confirmCollection.class);
            intent.putExtra("food", food);
            mCtx.startActivity(intent);
        }
    }



}