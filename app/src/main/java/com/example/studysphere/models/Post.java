package com.example.studysphere.models;

import com.google.firebase.Timestamp;

public class Post {

    private String title;
    private String description;
    private String authorName;
    private String authorId;
    private String program;
    private String fileUrl;
    private Timestamp timestamp;
    private int likesCount;
    private int commentsCount;

    // Required empty constructor for Firestore
    public Post() {}

    public Post(String title, String description, String authorName, String authorId,
                String program, String fileUrl, Timestamp timestamp,
                int likesCount, int commentsCount) {
        this.title = title;
        this.description = description;
        this.authorName = authorName;
        this.authorId = authorId;
        this.program = program;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthorName() { return authorName; }
    public String getAuthorId() { return authorId; }
    public String getProgram() { return program; }
    public String getFileUrl() { return fileUrl; }
    public Timestamp getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setProgram(String program) { this.program = program; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}
