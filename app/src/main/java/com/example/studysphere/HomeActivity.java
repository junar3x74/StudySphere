package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.studysphere.fragments.AccountFragment;
import com.example.studysphere.fragments.DownloadsFragment;
import com.example.studysphere.fragments.HomeFragment;
import com.example.studysphere.fragments.LibraryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;

public class HomeActivity extends AppCompatActivity {


    private DrawerLayout drawerLayout;
    private NavigationView drawerView;
    private NavigationView notificationDrawer;
    private BottomNavigationView bottomNavigation;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment libraryFragment = new LibraryFragment();
    private final Fragment downloadsFragment = new DownloadsFragment();
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

        // 🔧 Toolbar setup
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("StudySphere");

        // ☰ Drawer (left)
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawerView);


        // ☰ Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ☰ Drawer item click logic
        drawerView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_messages) {
                Toast.makeText(this, "📩 Messages clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.action_groups) {
                Toast.makeText(this, "👥 Group Chats clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });


        // Set default fragment to Home
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit();

        // Bottom Navigation logic
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = homeFragment;
            } else if (item.getItemId() == R.id.nav_library) {
                selectedFragment = libraryFragment;
            } else if (item.getItemId() == R.id.nav_create) {
                startActivity(new Intent(this, CreatePostActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_downloads) {
                selectedFragment = downloadsFragment;
            } else if (item.getItemId() == R.id.nav_account) {
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