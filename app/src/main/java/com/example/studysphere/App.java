package com.example.studysphere;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Disable App Check for Firebase Storage (dev-safe)

    }
}