package com.example.signinactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    ListView listView;
    PostClass postClassAdapter;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference myRef;
    ArrayList<String> userEmailFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageFromFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        listView = findViewById(R.id.listView);
        userCommentFromFB = new ArrayList<>();
        userEmailFromFB = new ArrayList<>();
        userImageFromFB = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        postClassAdapter = new PostClass(userEmailFromFB, userCommentFromFB, userImageFromFB, this);
        listView.setAdapter(postClassAdapter);

        //FireStore
        getDataFromFireStore();

        //Real Time Database
       // getDataFromFireBase();
    }
    public void getDataFromFireStore(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference =firebaseFirestore.collection("Posts");
        collectionReference
                .orderBy("Date :",Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                    Map<String,Object> data = snapshot.getData();
                    String comment = (String) data.get("Comment");
                    String email = (String) data.get("userEmail");
                    String downloadUrl = (String) data.get("downloadUrl");
                    userEmailFromFB.add(email);
                    userCommentFromFB.add(comment);
                    userImageFromFB.add(downloadUrl);
                    postClassAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    public void getDataFromFireBase() {
        DatabaseReference newRefrence = firebaseDatabase.getReference("Posts");
        newRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    HashMap<String ,String > hashMap = (HashMap<String, String>) ds.getValue();
                    userEmailFromFB.add(hashMap.get("userEmail"));
                    userCommentFromFB.add(hashMap.get("Comment"));
                    userImageFromFB.add(hashMap.get("downloadUrl"));
                    postClassAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
