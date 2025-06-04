package com.example.studysphere;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.adapters.PostAdapter;
import com.example.studysphere.models.PostItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PublicProfileActivity extends AppCompatActivity {



    private TextView fullNameText, studentIdText;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<PostItem> userPostItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        fullNameText = findViewById(R.id.publicProfileFullName);
        studentIdText = findViewById(R.id.publicProfileStudentID);
        postsRecyclerView = findViewById(R.id.publicUserPostsRecycler);

        db = FirebaseFirestore.getInstance();

        String studentID = getIntent().getStringExtra("studentID");
        if (studentID == null) {
            Toast.makeText(this, "No student specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserInfo(studentID);
        loadUserPosts(studentID);
    }

    private void loadUserInfo(String studentID) {
        db.collection("users")
                .whereEqualTo("studentID", studentID)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String fullName = doc.getString("fullName");
                        String student = doc.getString("studentID");

                        fullNameText.setText(fullName != null ? fullName : "Unknown User");
                        studentIdText.setText(student != null ? "Student ID: " + student : "Student ID: N/A");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show());
    }

    private void loadUserPosts(String studentID) {
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(userPostItems, this);
        postsRecyclerView.setAdapter(postAdapter);

        db.collection("posts")
                .whereEqualTo("studentID", studentID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    userPostItems.clear();
                    for (DocumentSnapshot doc : querySnapshots.getDocuments()) {
                        PostItem postItem = doc.toObject(PostItem.class);
                        if (postItem != null) {
                            userPostItems.add(postItem);
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show());
    }
}