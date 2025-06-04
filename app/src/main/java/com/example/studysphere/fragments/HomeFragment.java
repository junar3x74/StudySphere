package com.example.studysphere.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.studysphere.R;
import com.example.studysphere.adapters.PostAdapter;
import com.example.studysphere.models.PostItem;
import com.google.firebase.firestore.*;

import java.util.*;

public class HomeFragment extends Fragment {


    private RecyclerView feedRecyclerView;
    private ProgressBar progressBar;
    private PostAdapter postAdapter;
    private final List<PostItem> postList = new ArrayList<>();
    private FirebaseFirestore db;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        feedRecyclerView = view.findViewById(R.id.feedRecyclerView);
        progressBar = view.findViewById(R.id.feedLoadingBar); // Add this in layout

        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postAdapter = new PostAdapter(postList, requireContext());
        feedRecyclerView.setAdapter(postAdapter);

        loadPostsLive();
    }

    private void loadPostsLive() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error listening to posts", e);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    postList.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            PostItem post = doc.toObject(PostItem.class);
                            if (post != null) {
                                postList.add(post);
                            }
                        }
                    }

                    postAdapter.setPostList(postList);
                    progressBar.setVisibility(View.GONE);
                });
    }
}