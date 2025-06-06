package com.example.studysphere;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.studysphere.fragments.AccountFragment;
import com.example.studysphere.fragments.CreatePostFragment;
import com.example.studysphere.fragments.DownloadsFragment;
import com.example.studysphere.fragments.HomeFragment;
import com.example.studysphere.fragments.LibraryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment libraryFragment = new LibraryFragment();
    private final Fragment downloadsFragment = new DownloadsFragment();
    private final Fragment createPostFragment = new CreatePostFragment();
    private final Fragment profileFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔧 Force Firestore to fetch fresh data
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build();
        firestore.setFirestoreSettings(settings);

        // Top bar setup
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("StudySphere");

        // Default screen
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit();

        // Bottom navigation logic
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = homeFragment;
            } else if (id == R.id.nav_library) {
                selectedFragment = libraryFragment;
            } else if (id == R.id.nav_create) {
                selectedFragment = createPostFragment;
            } else if (id == R.id.nav_downloads) {
                selectedFragment = downloadsFragment;
            } else if (id == R.id.nav_account) {
                selectedFragment = profileFragment;
            }


            if (selectedFragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }
}
