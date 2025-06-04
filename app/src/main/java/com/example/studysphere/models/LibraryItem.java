package com.example.studysphere.models;

public class LibraryItem {
    private String title;
    private String program;
    private String description;
    private String fileURL;

    // Needed for Firestore deserialization
    public LibraryItem() {}

    public LibraryItem(String title, String program, String description, String fileURL) {
        this.title = title;
        this.program = program;
        this.description = description;
        this.fileURL = fileURL;
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

    public String getFileURL() {
        return fileURL;
    }
}
