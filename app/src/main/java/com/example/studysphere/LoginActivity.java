package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView goToSignup, forgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // UI Components
        inputEmail = findViewById(R.id.loginEmail);
        inputPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        goToSignup = findViewById(R.id.goToSignup);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Go to signup
        goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            // Optional: You can clear the stack if needed when going from login → signup
            startActivity(intent);
            finish();
        });

        // Forgot password
        forgotPassword.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                inputEmail.setError("Enter your email to reset password");
                inputEmail.requestFocus();
                return;
            }

            LoadingDialogHelper.show(LoginActivity.this, "Sending reset email...");

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        LoadingDialogHelper.hide();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
            return;
        }

        LoadingDialogHelper.show(LoginActivity.this, "Logging in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    LoadingDialogHelper.hide();
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // ✅ Prevent going back to login with BACK button
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}