package com.example.giveapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SearchFriends extends AppCompatActivity {

    private static final String TAG = "SearchFriends";

    private RecyclerView recyclerView;
    private FriendSearchAdapter adaptor;
    private ArrayList<Users> userList;
    private FirebaseFirestore db;

    private EditText search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        db = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptor = new FriendSearchAdapter(this, userList);
        recyclerView.setAdapter(adaptor);


        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                            for(DocumentSnapshot d : list){
                                Users u = d.toObject(Users.class);
                                u.setId(d.getId());
                                userList.add(u);

                            }
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });

        Client client = new Client("A5FO78D1EG", "006b477cd838e6297ad7a265a88aef92");
        final Index index = client.getIndex("user_search");

        search = findViewById(R.id.searchView);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Query query = new com.algolia.search.saas.Query(s.toString())
                        .setAttributesToRetrieve("fName", "email", "imageUrl")
                        .setHitsPerPage(50);

                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                        try {

                            JSONArray hits = jsonObject.getJSONArray("hits");
                            userList.clear();

                            for (int i = 0; i < hits.length(); i++) {

                                JSONObject jsObj = hits.getJSONObject(i);
                                String fName = jsObj.getString("fName");
                                String email = jsObj.getString("email");
                                String imageUrl = jsObj.getString("imageUrl");
                                Users u = new Users(fName, email, imageUrl);
                                userList.add(u);

                            }

                            adaptor = new FriendSearchAdapter(getApplicationContext(), userList);
                            recyclerView.setAdapter(adaptor);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            }
        });


    }





}
