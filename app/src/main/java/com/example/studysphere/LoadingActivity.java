package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studysphere.HomeActivity;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Simulate loading delay (e.g., fetch profile, sync Firestore)
        new Handler().postDelayed(() -> {
            startActivity(new Intent(LoadingActivity.this, HomeActivity.class));
            finish();
        }, 2000); // 2-second delay
    }
}
