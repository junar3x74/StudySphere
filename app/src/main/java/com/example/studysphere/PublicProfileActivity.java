package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studysphere.adapters.PostAdapter;
import com.example.studysphere.models.PostItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PublicProfileActivity extends AppCompatActivity {

    private static final String TAG = "PublicProfileActivity"; // Define a TAG for logging

    private ImageView publicProfileAvatar;
    private TextView fullNameText, studentIdText;
    private Button btnEditPublicProfile;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<PostItem> userPostItems = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // This will now store the Firebase Auth UID directly from the document's ID
    private String viewedUserDocumentId = null; // Renamed for clarity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        publicProfileAvatar = findViewById(R.id.publicProfileAvatar);
        fullNameText = findViewById(R.id.publicProfileFullName);
        studentIdText = findViewById(R.id.publicProfileStudentID);
        btnEditPublicProfile = findViewById(R.id.btnEditPublicProfile);
        postsRecyclerView = findViewById(R.id.publicUserPostsRecycler);

        btnEditPublicProfile.setVisibility(View.GONE); // Hidden by default

        String studentIDPassed = getIntent().getStringExtra("studentID"); // Renamed to avoid confusion
        if (studentIDPassed == null || studentIDPassed.isEmpty()) {
            Toast.makeText(this, "No student specified.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Received studentID field value: " + studentIDPassed);

        loadUserInfoAndPosts(studentIDPassed);

        btnEditPublicProfile.setOnClickListener(v -> {
            Intent intent = new Intent(PublicProfileActivity.this, EditProfileActivity.class);
            // No need to pass anything, EditProfileActivity will directly use currentUser.getUid()
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String studentIDPassed = getIntent().getStringExtra("studentID");
        if (studentIDPassed != null && !studentIDPassed.isEmpty()) {
            // Reload data when returning to this activity, in case profile was edited
            loadUserInfoAndPosts(studentIDPassed);
        }
    }

    private void loadUserInfoAndPosts(String studentIDToQuery) {
        // Now, we query for user information where the 'studentID' field matches.
        // We expect only one result, and its Document ID should be the user's Auth UID.
        db.collection("users")
                .whereEqualTo("studentID", studentIDToQuery)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        viewedUserDocumentId = doc.getId(); // Get the Document ID (which is now the Auth UID)

                        String fullName = doc.getString("fullName");
                        String student = doc.getString("studentID"); // Retrieve studentID field
                        String profileImageUrl = doc.getString("profileImageUrl");

                        fullNameText.setText(fullName != null ? fullName : "Unknown User");
                        studentIdText.setText(student != null ? "Student ID: " + student : "Student ID: N/A");

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .error(R.drawable.ic_default_avatar)
                                    .into(publicProfileAvatar);
                        } else {
                            publicProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                        }

                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        // --- IMPORTANT LOGGING FOR DEBUGGING ---
                        if (currentUser != null) {
                            Log.d(TAG, "Current User UID (Auth): " + currentUser.getUid());
                        } else {
                            Log.d(TAG, "No user currently logged in.");
                        }
                        Log.d(TAG, "Viewed User Document ID (which is now Auth UID): " + viewedUserDocumentId);
                        // --- END IMPORTANT LOGGING ---


                        // Check if the viewed profile's Document ID matches the current user's Auth UID
                        if (currentUser != null && viewedUserDocumentId != null && currentUser.getUid().equals(viewedUserDocumentId)) {
                            btnEditPublicProfile.setVisibility(View.VISIBLE); // Show the button
                            Log.d(TAG, "Edit Profile button VISIBLE: Current User Auth UID matches Viewed User's Document ID.");
                        } else {
                            btnEditPublicProfile.setVisibility(View.GONE); // Hide if not current user
                            Log.d(TAG, "Edit Profile button GONE: Mismatch or no user logged in.");
                        }

                        // Load posts associated with the studentID
                        loadUserPosts(studentIDToQuery); // Still using the studentID field to query posts

                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                        fullNameText.setText("User Not Found");
                        studentIdText.setText("Student ID: N/A");
                        publicProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                        btnEditPublicProfile.setVisibility(View.GONE);
                        userPostItems.clear();
                        if (postAdapter != null) {
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load user info: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    fullNameText.setText("Error Loading Profile");
                    studentIdText.setText("Student ID: Error");
                    publicProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                    btnEditPublicProfile.setVisibility(View.GONE);
                    userPostItems.clear();
                    if (postAdapter != null) {
                        postAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void loadUserPosts(String studentID) {
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(userPostItems, this);
        postsRecyclerView.setAdapter(postAdapter);

        db.collection("posts")
                .whereEqualTo("studentID", studentID) // Assuming posts still have a studentID field
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
                        Toast.makeText(this, "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}