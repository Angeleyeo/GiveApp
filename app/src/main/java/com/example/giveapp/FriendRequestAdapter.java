package com.example.giveapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<Users> userList;

    public FriendRequestAdapter(Context mCtx, List<Users> userList) {
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
            Users user = userList.get(getAbsoluteAdapterPosition());
            Intent intent = new Intent(mCtx, FriendRequests_details.class);
            intent.putExtra("user", user);
            mCtx.startActivity(intent);
        }
    }

}

