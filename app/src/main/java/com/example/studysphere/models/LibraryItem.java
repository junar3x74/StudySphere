package com.example.studysphere.models;

public class LibraryItem {
    private String title;
    private String program;
    private String description;
    private String fileUrl;

    // Needed for Firestore deserialization
    public LibraryItem() {}

    public LibraryItem(String title, String program, String description, String fileUrl) {
        this.title = title;
        this.program = program;
        this.description = description;
        this.fileUrl = fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getProgram() {
        return program;
    }

    public String getDescription() {
        return description;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
