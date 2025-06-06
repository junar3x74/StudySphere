package com.example.studysphere.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studysphere.LoginActivity;
import com.example.studysphere.R;
import com.example.studysphere.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreatePostFragment extends Fragment {

    private EditText inputTitle, inputDescription;
    private Button btnSelectFile, btnSubmitPost;
    private TextView fileNamePreview;

    private Uri fileUri;
    private String selectedFileName = "No file selected";

    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    public CreatePostFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            View guestView = inflater.inflate(R.layout.fragment_guest_account, container, false);
            Button btnLogin = guestView.findViewById(R.id.btnLogin);
            Button btnSignup = guestView.findViewById(R.id.btnSignup);

            btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
            btnSignup.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignupActivity.class)));

            return guestView;
        }

        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) return;

        inputTitle = view.findViewById(R.id.inputTitle);
        inputDescription = view.findViewById(R.id.inputDescription);
        btnSelectFile = view.findViewById(R.id.btnSelectFile);
        btnSubmitPost = view.findViewById(R.id.btnSubmitPost);
        fileNamePreview = view.findViewById(R.id.fileNamePreview);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData();
                        if (fileUri != null) {
                            selectedFileName = getFileNameFromUri(fileUri);
                            fileNamePreview.setText(selectedFileName);
                        }
                    }
                });

        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnSubmitPost.setOnClickListener(v -> uploadPost());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private String getFileNameFromUri(Uri uri) {
        String result = "attached_file";
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            result = new File(uri.getPath()).getName();
        }
        return result;
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
            Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show();
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
                        String safeFileName = selectedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                        String filePath = "uploaded_files/" + user.getUid() + "/" + System.currentTimeMillis() + "_" + safeFileName;
                        StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                                .child("uploads/" + filePath);

                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
                            if (inputStream != null) {
                                UploadTask uploadTask = fileRef.putStream(inputStream);

                                uploadTask.addOnSuccessListener(taskSnapshot ->
                                        fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                                                savePostToFirestore(title, description, uri.toString(), user, studentID, fullName, program)
                                        )
                                ).addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    btnSubmitPost.setEnabled(true);
                                    btnSubmitPost.setText("Post");
                                });
                            } else {
                                throw new FileNotFoundException("Unable to open file stream");
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Error accessing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            btnSubmitPost.setEnabled(true);
                            btnSubmitPost.setText("Post");
                        }
                    } else {
                        savePostToFirestore(title, description, null, user, studentID, fullName, program);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
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
        post.put("studentID", studentID);
        post.put("authUID", user.getUid());
        post.put("fullName", fullName);
        post.put("program", program);
        post.put("timestamp", FieldValue.serverTimestamp());
        post.put("likesCount", 0);
        post.put("commentsCount", 0);

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Post created!", Toast.LENGTH_SHORT).show();
                    inputTitle.setText("");
                    inputDescription.setText("");
                    fileUri = null;
                    fileNamePreview.setText("No file selected");
                    btnSubmitPost.setEnabled(true);
                    btnSubmitPost.setText("Post");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
                    btnSubmitPost.setEnabled(true);
                    btnSubmitPost.setText("Post");
                });
    }
}