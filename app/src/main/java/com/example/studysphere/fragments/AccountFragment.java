package com.example.studysphere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.studysphere.HomeActivity;
import com.example.studysphere.LoginActivity;
import com.example.studysphere.PublicProfileActivity;
import com.example.studysphere.R;
import com.example.studysphere.SignupActivity;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class AccountFragment extends Fragment {

    private TextView profileFullName, profileStudentId, profileEmail;
    private Button btnLogout, btnChangeEmail, btnChangePassword, btnViewProfile;
    private Switch liteModeSwitch;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public AccountFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            user.reload().addOnSuccessListener(unused -> {
                profileEmail.setText("Email: " + user.getEmail());
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        if (user == null) {
            // Guest layout
            View guestView = inflater.inflate(R.layout.fragment_guest_account, container, false);
            Button btnLogin = guestView.findViewById(R.id.btnLogin);
            Button btnSignup = guestView.findViewById(R.id.btnSignup);

            btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
            btnSignup.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignupActivity.class)));

            return guestView;
        }

        // Authenticated user layout
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileFullName = view.findViewById(R.id.profileFullName);
        profileStudentId = view.findViewById(R.id.profileStudentId);
        profileEmail = view.findViewById(R.id.profileEmail);
        btnViewProfile = view.findViewById(R.id.btnViewProfile);
        btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        liteModeSwitch = view.findViewById(R.id.switchLiteMode);

        profileEmail.setText("Email: " + user.getEmail());

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String fullName = document.getString("fullName");
                        String studentId = document.getString("studentID");

                        profileFullName.setText(fullName != null ? fullName : "Full Name: Not found");
                        profileStudentId.setText(studentId != null ? "Student ID: " + studentId : "Student ID: Not found");
                    } else {
                        profileFullName.setText("Full Name: Document missing");
                        profileStudentId.setText("Student ID: Document missing");
                    }
                })
                .addOnFailureListener(e -> {
                    profileFullName.setText("Full Name: Error");
                    profileStudentId.setText("Student ID: Error");
                    Toast.makeText(getContext(), "Failed to fetch profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        btnViewProfile.setOnClickListener(v -> {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String studentId = document.getString("studentID");
                            if (studentId != null && !studentId.isEmpty()) {
                                Intent intent = new Intent(requireContext(), PublicProfileActivity.class);
                                intent.putExtra("studentID", studentId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "No Student ID found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        btnChangeEmail.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Email");

            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 0);

            EditText inputEmail = new EditText(requireContext());
            inputEmail.setHint("New Email");
            layout.addView(inputEmail);

            EditText inputPassword = new EditText(requireContext());
            inputPassword.setHint("Current Password");
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(inputPassword);

            builder.setView(layout);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newEmail = inputEmail.getText().toString().trim();
                String currentPassword = inputPassword.getText().toString();

                if (newEmail.isEmpty() || currentPassword.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnSuccessListener(unused -> {
                    user.verifyBeforeUpdateEmail(newEmail)
                            .addOnSuccessListener(unused2 -> {
                                Toast.makeText(getContext(),
                                        "Verification email sent to " + newEmail + ". Please verify to complete the update.",
                                        Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(),
                                        "Verification failed: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        btnChangePassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Change Password");

            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 0);

            EditText currentPassInput = new EditText(requireContext());
            currentPassInput.setHint("Current Password");
            currentPassInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(currentPassInput);

            EditText newPassInput = new EditText(requireContext());
            newPassInput.setHint("New Password");
            newPassInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            layout.addView(newPassInput);

            builder.setView(layout);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String currentPass = currentPassInput.getText().toString();
                String newPass = newPassInput.getText().toString();

                if (currentPass.isEmpty() || newPass.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Auth failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        });

        liteModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(getContext(),
                    isChecked ? "Lite Mode enabled" : "Lite Mode disabled",
                    Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
