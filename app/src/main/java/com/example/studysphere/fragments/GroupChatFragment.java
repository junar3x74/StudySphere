package com.example.studysphere.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GroupChatFragment extends Fragment {

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        TextView textView = new TextView(getContext());
        textView.setText("Group chats will appear here.");
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setPadding(32, 32, 32, 32);

        return textView;
    }
}