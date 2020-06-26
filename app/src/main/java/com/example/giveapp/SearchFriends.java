package com.example.giveapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFriends extends AppCompatActivity {

    FirebaseFirestore fStore;
    RecyclerView friendsRV;
    private String list_fName;
    private String list_email;
    private FirestoreRecyclerAdapter adapter;
    SearchView searchView;

    ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        userList = new ArrayList<>();

        fStore = FirebaseFirestore.getInstance();
        friendsRV = findViewById(R.id.friendsRecyclerView);
        //searchView = findViewById(R.id.searchView);

        //query
        Query query = fStore.collection("users");
        //recycler options
        /**query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                adapter = new FriendSearchAdapter(task.getResult().toObjects(Users.class));
                friendsRV.setAdapter(adapter);
            }
        });**/

        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
               .setQuery(query,
                        Users.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.list_fName.setText(model.getName());
                holder.list_email.setText(model.getEmail());
            }

        };

        friendsRV.setHasFixedSize(true);
        friendsRV.setLayoutManager(new LinearLayoutManager(this));
        friendsRV.setAdapter(adapter);


        /**searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return true;
            }
        });**/

    }

    private class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView list_fName;
        private TextView list_email;

        public UsersViewHolder(View itemView) {
            super(itemView);

            list_fName = itemView.findViewById(R.id.list_fName);
            list_email = itemView.findViewById(R.id.list_email);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


}