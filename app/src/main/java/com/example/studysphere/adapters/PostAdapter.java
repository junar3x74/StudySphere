package com.example.studysphere.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.PublicProfileActivity;
import com.example.studysphere.R;
import com.example.studysphere.models.PostItem;
import com.google.firebase.Timestamp;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostItem> postItemList;
    private Context context;

    public PostAdapter(List<PostItem> postItemList, Context context) {
        this.postItemList = postItemList;
        this.context = context;
    }

    public void setPostList(List<PostItem> postItemList) {
        this.postItemList = postItemList;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postDescription, postAuthor, postProgram;

        public PostViewHolder(View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            postAuthor = itemView.findViewById(R.id.postAuthor);
            postProgram = itemView.findViewById(R.id.postProgram);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem postItem = postItemList.get(position);

        holder.postTitle.setText(postItem.getTitle());
        holder.postDescription.setText(postItem.getDescription());
        holder.postProgram.setText(postItem.getProgram());

        // Format timestamp
        Timestamp timestamp = postItem.getTimestamp();
        String timeAgo = "";
        if (timestamp != null) {
            long now = System.currentTimeMillis();
            long postTime = timestamp.toDate().getTime();
            timeAgo = DateUtils.getRelativeTimeSpanString(postTime, now, DateUtils.MINUTE_IN_MILLIS).toString();
        }

        holder.postAuthor.setText("Posted by " + postItem.getFullName() + " · " + timeAgo);

        // Author name is clickable
        holder.postAuthor.setOnClickListener(v -> {
            Intent intent = new Intent(context, PublicProfileActivity.class);
            intent.putExtra("studentID", postItem.getStudentID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postItemList.size();
    }
}