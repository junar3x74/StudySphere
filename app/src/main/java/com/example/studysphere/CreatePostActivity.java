package com.example.studysphere;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private EditText inputTitle, inputDescription;
    private Button btnSelectFile, btnSubmitPost;
    private TextView fileNamePreview;

    private static final int PICK_FILE_REQUEST = 101;
    private Uri fileUri;
    private String selectedFileName = "No file selected";

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        fileNamePreview = findViewById(R.id.fileNamePreview);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnSubmitPost.setOnClickListener(v -> uploadPost());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                selectedFileName = fileUri.getLastPathSegment();
                fileNamePreview.setText(selectedFileName);
            }
        }
    }

    private void uploadPost() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            inputTitle.setError("Title is required");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitPost.setEnabled(false);
        btnSubmitPost.setText("Posting...");

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    String fullName = doc.getString("fullName");
                    String studentID = doc.getString("studentID");
                    String program = doc.getString("program");

                    if (fileUri != null) {
                        String filename = "posts/" + System.currentTimeMillis() + "_" + selectedFileName;
                        StorageReference fileRef = storage.getReference().child(filename);

                        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                                fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                                        savePostToFirestore(title, description, uri.toString(), user, studentID, fullName, program)
                                )
                        ).addOnFailureListener(e -> {
                            Toast.makeText(this, "File upload failed", Toast.LENGTH_SHORT).show();
                            btnSubmitPost.setEnabled(true);
                            btnSubmitPost.setText("Post");
                        });
                    } else {
                        savePostToFirestore(title, description, null, user, studentID, fullName, program);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
                    btnSubmitPost.setEnabled(true);
                    btnSubmitPost.setText("Post");
                });
    }

    private void savePostToFirestore(String title, String description, String fileURL, FirebaseUser user,
                                     String studentID, String fullName, String program) {
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        post.put("fileURL", fileURL);
        post.put("studentID", studentID);    // your app's ID, like "22-12345"
        post.put("authUID", user.getUid());  // Firebase's UID
        post.put("fullName", fullName);
        post.put("program", program);
        post.put("timestamp", FieldValue.serverTimestamp());
        post.put("likesCount", 0);
        post.put("commentsCount", 0);

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreatePostActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    btnSubmitPost.setEnabled(true);
                    btnSubmitPost.setText("Post");
                });
    }
}
