package com.example.studysphere;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar; // Import MaterialToolbar
import com.example.studysphere.adapters.MessagesPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MessagesActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MessagesPagerAdapter adapter;
    private MaterialToolbar messagesToolbar; // Declare MaterialToolbar

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        tabLayout = findViewById(R.id.messagesTabLayout);
        viewPager = findViewById(R.id.messagesViewPager);
        messagesToolbar = findViewById(R.id.messagesToolbar); // Initialize MaterialToolbar

        // Set the toolbar as the support action bar
        setSupportActionBar(messagesToolbar);

        // Handle the back arrow click
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setDisplayShowHomeEnabled(true); // Show the back button
        }

        messagesToolbar.setNavigationOnClickListener(v -> {
            onBackPressed(); // This will navigate back to the previous activity
        });


        adapter = new MessagesPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Chats");
            } else {
                tab.setText("Group Chats");
            }
        }).attach();
    }
}