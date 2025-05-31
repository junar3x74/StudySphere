package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studysphere.fragments.HomeFragment;
import com.example.studysphere.fragments.LibraryFragment;
import com.example.studysphere.fragments.ProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.studysphere.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private BottomNavigationView bottomNavigation;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment libraryFragment = new LibraryFragment();
    private final Fragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        topAppBar = findViewById(R.id.topAppBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set default fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit();

        // Top AppBar menu actions
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_lite_mode) {
                // TODO: Toggle Lite Mode logic
                Toast.makeText(HomeActivity.this, "Lite Mode toggled", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Bottom navigation selection
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = homeFragment;

            } else if (id == R.id.nav_library) {
                selectedFragment = libraryFragment;

            } else if (id == R.id.nav_create) {
                startActivity(new Intent(HomeActivity.this, CreatePostActivity.class));
                return true;

            } else if (id == R.id.nav_chat) {
                Toast.makeText(this, "Chat coming soon", Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.nav_inbox) {
                Toast.makeText(this, "Inbox feature coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }

            return false;

        });

    }
}
