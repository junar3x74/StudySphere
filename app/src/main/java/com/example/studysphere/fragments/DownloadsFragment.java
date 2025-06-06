package com.example.studysphere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView; // Import TextView
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.studysphere.LoginActivity;
import com.example.studysphere.SignupActivity;
import com.example.studysphere.R;
import com.example.studysphere.adapters.DownloadsAdapter;
import com.example.studysphere.models.DownloadItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.*;

public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DownloadsAdapter adapter;
    private List<DownloadItem> downloadsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView emptyDownloadsTextView; // Declare TextView

    public DownloadsFragment() {}

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

        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerView = view.findViewById(R.id.downloadsRecyclerView);
        emptyDownloadsTextView = view.findViewById(R.id.emptyDownloadsTextView); // Initialize TextView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DownloadsAdapter(requireContext(), downloadsList);
        recyclerView.setAdapter(adapter);

        loadDownloads();

        return view;
    }

    private void loadDownloads() {
        File downloadsDir = new File(requireContext().getExternalFilesDir(null), "downloads");

        if (!downloadsDir.exists() || !downloadsDir.isDirectory()) {
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            emptyDownloadsTextView.setVisibility(View.VISIBLE); // Show TextView
            emptyDownloadsTextView.setText("No downloaded files found."); // Set text
            return;
        }

        File[] files = downloadsDir.listFiles();
        if (files == null || files.length == 0) {
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            emptyDownloadsTextView.setVisibility(View.VISIBLE); // Show TextView
            emptyDownloadsTextView.setText("No downloaded files found."); // Set text
            return;
        }

        // If files are found, make sure RecyclerView is visible and TextView is hidden
        recyclerView.setVisibility(View.VISIBLE);
        emptyDownloadsTextView.setVisibility(View.GONE);

        downloadsList.clear();
        for (File file : files) {
            String name = file.getName();
            String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
            String category = ext.equals("pdf") ? "Documents" : ext.equals("mp3") ? "Audio" : ext.equals("mp4") ? "Video" : "Others";
            downloadsList.add(new DownloadItem(name, file.getAbsolutePath(), ext, category, file.length(), file.lastModified()));
        }

        adapter.setDownloadList(downloadsList);
    }
}