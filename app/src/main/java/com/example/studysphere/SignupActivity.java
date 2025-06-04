package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText fullName, studentId, email, password, confirmPassword;
    private Spinner spinnerProgram;
    private Button btnSignUp;
    private TextView goToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Elements
        studentId = findViewById(R.id.signupStudentId);
        fullName = findViewById(R.id.signupFullName);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        confirmPassword = findViewById(R.id.signupConfirmPassword);
        spinnerProgram = findViewById(R.id.spinnerProgram);
        btnSignUp = findViewById(R.id.btnSignUp);
        goToLogin = findViewById(R.id.goToLogin);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.program_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(adapter);

        // Go to login link
        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Sign Up button logic
        btnSignUp.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        String name = fullName.getText().toString().trim();
        String studentID = studentId.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userConfirm = confirmPassword.getText().toString().trim();
        String program = spinnerProgram.getSelectedItem().toString();

        if (program.equals("--Select Program--")) {
            Toast.makeText(SignupActivity.this, "Please select a valid program", Toast.LENGTH_SHORT).show();
            return;
        }


        // Basic validation
        if (TextUtils.isEmpty(name)) {
            fullName.setError("Full name required");
            return;
        }

        if (TextUtils.isEmpty(studentID)) {
            studentId.setError("Student ID required");
            return;
        } else if (!studentID.matches("^\\d{2}-\\d{5}$")) {
            studentId.setError("Invalid format. Use 00-00000");
            return;
        }


        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(userPassword) || userPassword.length() < 6) {
            password.setError("Password must be 6+ characters");
            return;
        }

        if (!userPassword.equals(userConfirm)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        // Show loading dialog
        LoadingDialogHelper.show(SignupActivity.this, "Creating account...");

        // Create user
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        // Save user profile to Firestore
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullName", name);
                        userData.put("studentID", studentID);
                        userData.put("email", userEmail);
                        userData.put("program", program);
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(unused -> {
                                    LoadingDialogHelper.hide();
                                    Toast.makeText(SignupActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    LoadingDialogHelper.hide();
                                    Toast.makeText(SignupActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        LoadingDialogHelper.hide();
                        Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
