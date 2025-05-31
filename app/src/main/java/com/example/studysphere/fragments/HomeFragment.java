package com.example.studysphere.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.studysphere.R;
import com.example.studysphere.adapters.PostAdapter;
import com.example.studysphere.models.Post;
import com.google.firebase.firestore.*;

import java.util.*;

public class HomeFragment extends Fragment {

    private RecyclerView feedRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

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

        // Setup RecyclerView
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList);
        feedRecyclerView.setAdapter(postAdapter);

        loadPostsFromFirestore();
    }

    private void loadPostsFromFirestore() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    postList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        postList.add(post);
                    }
                    postAdapter.setPostList(postList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading posts", e);
                });
    }
}
