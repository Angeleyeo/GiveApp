package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdaptor extends RecyclerView.Adapter<FoodAdaptor.ProductViewHolder> {

    private Context mCtx;
    private static List<Food> foodList;

    public FoodAdaptor(Context mCtx, List<Food> foodList) {
        this.mCtx = mCtx;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodAdaptor.ProductViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.food_view, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdaptor.ProductViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.foodName.setText(food.getFoodName());
        holder.foodPrice.setText("S$" + food.getFoodPrice());
        holder.foodQty.setText(food.getFoodQty());

        Picasso.get().load(food.getFoodLogo()).into(holder.foodLogo);

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


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
            Food food = foodList.get(getAbsoluteAdapterPosition());
            Intent intent;
            intent = new Intent(mCtx, foodDetails.class);
            intent.putExtra("food", food);
            mCtx.startActivity(intent);
        }
    }

}
