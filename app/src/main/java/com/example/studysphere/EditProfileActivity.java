package com.example.studysphere;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private ImageView editProfileAvatar;
    private TextView btnEditPhoto;
    private TextInputEditText editFullName, editStudentID; // Removed editBio
    private Button btnSaveChanges;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private DocumentReference currentUserDocRef;
    private StorageReference profileImagesRef;

    private Uri selectedImageUri;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to edit your profile.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        currentUserDocRef = db.collection("users").document(currentUser.getUid());
        profileImagesRef = storage.getReference().child("profile_images").child(currentUser.getUid());


        Toolbar toolbar = findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Keeps your custom title
            // If you want the toolbar back button to be white (matching your toolbar text),
            // you might need to set the navigation icon tint, or ensure your
            // ThemeOverlay.AppCompat.ActionBar correctly sets colorControlNormal.
            // For now, it should default to what your theme provides.
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // Sets the back button action

        editProfileAvatar = findViewById(R.id.editProfileAvatar);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);
        editFullName = findViewById(R.id.editFullName);
        editStudentID = findViewById(R.id.editStudentID);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBar);


        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        editProfileAvatar.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        loadProfileData();

        btnEditPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadProfileData() {
        progressBar.setVisibility(View.VISIBLE);
        btnSaveChanges.setEnabled(false);

        if (currentUser == null) {
            Toast.makeText(this, "You are not logged in.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            btnSaveChanges.setEnabled(true);
            finish();
            return;
        }

        currentUserDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String studentID = documentSnapshot.getString("studentID");
                        // Removed bio retrieval
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        editFullName.setText(fullName);
                        editStudentID.setText(studentID);
                        // Removed setting bio text

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .error(R.drawable.ic_default_avatar)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Log.e(TAG, "Glide image load failed: " + e.getMessage());
                                            progressBar.setVisibility(View.GONE);
                                            btnSaveChanges.setEnabled(true);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            btnSaveChanges.setEnabled(true);
                                            return false;
                                        }
                                    })
                                    .into(editProfileAvatar);
                        } else {
                            editProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                            progressBar.setVisibility(View.GONE);
                            btnSaveChanges.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(this, "Your profile data not found. Please create a profile first.", Toast.LENGTH_LONG).show();
                        editProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                        progressBar.setVisibility(View.GONE);
                        btnSaveChanges.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading profile data: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to load profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    editProfileAvatar.setImageResource(R.drawable.ic_default_avatar);
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);
                });
    }

    private void saveChanges() {
        String newFullName = editFullName.getText().toString().trim();
        String newStudentID = editStudentID.getText().toString().trim();
        // Removed bio retrieval

        if (newFullName.isEmpty() || newStudentID.isEmpty()) {
            Toast.makeText(this, "Full Name and Student ID cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newStudentID.matches("\\d{2}-\\d{5}")) { // Matches XX-XXXXX format
            editStudentID.setError("Invalid Student ID format (e.g., 00-00000)");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSaveChanges.setEnabled(false);

        // Passed null for bio since it's no longer used
        proceedWithSave(newFullName, newStudentID, null);
    }

    // Adjusted method signature to no longer expect bio as a mandatory parameter
    private void proceedWithSave(String fullName, String studentID, @Nullable String bio) {
        if (selectedImageUri != null) {
            uploadProfileImage(fullName, studentID, bio);
        } else {
            // Passed null for bio
            updateProfileData(fullName, studentID, bio, null);
        }
    }

    private void uploadProfileImage(String fullName, String studentID, @Nullable String bio) { // Adjusted signature
        StorageReference imageRef = profileImagesRef.child("avatar_" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Passed null for bio
                        updateProfileData(fullName, studentID, bio, imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL: " + e.getMessage(), e);
                        Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnSaveChanges.setEnabled(true);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to upload profile image.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);
                });
    }

    private void updateProfileData(String fullName, String studentID, @Nullable String bio, @Nullable String imageUrl) { // Adjusted signature
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);
        updates.put("studentID", studentID);
        // Removed putting bio in updates as it's no longer stored
        if (imageUrl != null) {
            updates.put("profileImageUrl", imageUrl);
        }

        currentUserDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update profile data: " + e.getMessage(), e);
                    Toast.makeText(this, "Failed to save changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSaveChanges.setEnabled(true);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}