package com.example.studysphere.models;

import com.google.firebase.Timestamp;

public class PostItem {

    private String postID;
    private String studentID;   // Replaces userID
    private String fullName;
    private String title;
    private String description;
    private String program;
    private String fileURL;
    private Timestamp timestamp;
    private int likesCount;
    private int commentsCount;

    // Required empty constructor for Firestore
    public PostItem() {}

    public PostItem(String postID, String studentID, String fullName,
                    String title, String description, String program,
                    String fileURL, Timestamp timestamp,
                    int likesCount, int commentsCount) {
        this.postID = postID;
        this.studentID = studentID;
        this.fullName = fullName;
        this.title = title;
        this.description = description;
        this.program = program;
        this.fileURL = fileURL;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    // Getters
    public String getPostID() { return postID; }
    public String getStudentID() { return studentID; }
    public String getFullName() { return fullName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getProgram() { return program; }
    public String getFileURL() { return fileURL; }
    public Timestamp getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }

    // Setters
    public void setPostID(String postID) { this.postID = postID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setProgram(String program) { this.program = program; }
    public void setFileURL(String fileURL) { this.fileURL = fileURL; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}