package com.example.studysphere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.studysphere.LoginActivity;
import com.example.studysphere.R;
import com.example.studysphere.SignupActivity;
import com.example.studysphere.adapters.LibraryAdapter;
import com.example.studysphere.models.LibraryItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibraryAdapter adapter;
    private List<LibraryItem> itemList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    public LibraryFragment() {
        // Required empty constructor
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

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerView = view.findViewById(R.id.libraryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();
        adapter = new LibraryAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadLibraryResources();

        return view;
    }

    private void loadLibraryResources() {
        db.collection("library_resources")
                .orderBy("datePublished", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        LibraryItem item = doc.toObject(LibraryItem.class);
                        if (item != null) {
                            itemList.add(item);
                        } else {
                            android.util.Log.w("LibraryDebug", "Document " + doc.getId() + " could not be mapped to LibraryItem");
                        }
                    }

                    if (itemList.isEmpty()) {
                        Toast.makeText(getContext(), "No resources found.", Toast.LENGTH_SHORT).show();
                    }

                    adapter.setItemList(itemList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load resources", Toast.LENGTH_SHORT).show();
                });
    }
}
