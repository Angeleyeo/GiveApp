package com.example.giveapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.UsersViewHolder> implements Filterable {
    private List<Users> mTubeList;
    private List<Users> mTubeListFiltered;

    public FriendSearchAdapter(List<Users> tubeList){
        mTubeList = tubeList;
        mTubeListFiltered = tubeList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty()){
                    mTubeListFiltered = mTubeList;
                } else {
                    List<Users> filteredList = new ArrayList<>();
                    for(Users tube: mTubeList){
                        if(tube.getName().toLowerCase().contains(pattern) || tube.getName().toLowerCase().contains(pattern)) {
                            filteredList.add(tube);
                        }
                    }
                    mTubeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mTubeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mTubeListFiltered = (ArrayList<Users>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public FriendSearchAdapter.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchAdapter.UsersViewHolder holder, int position) {

        Users user = mTubeListFiltered.get(position);
        holder.list_fName.setText(user.getName());
        holder.list_email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView list_fName;
        private TextView list_email;

        public UsersViewHolder(View itemView) {
            super(itemView);

            list_fName = itemView.findViewById(R.id.list_fName);
            list_email = itemView.findViewById(R.id.list_email);
        }
    }

}
**/