package com.example.studysphere.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studysphere.fragments.ChatFragment;
import com.example.studysphere.fragments.GroupChatFragment;

public class MessagesPagerAdapter extends FragmentStateAdapter {

    public MessagesPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ChatFragment();
        } else {
            return new GroupChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Chat and Group Chat
    }
}
