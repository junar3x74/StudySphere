package com.example.studysphere.models;

public class DownloadItem {
    private String fileName;
    private String filePath;
    private String fileType;
    private String category;
    private long fileSize; // in bytes
    private long dateDownloaded; // timestamp


    public DownloadItem() {}

    public DownloadItem(String fileName, String filePath, String fileType, String category, long fileSize, long dateDownloaded) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.category = category;
        this.fileSize = fileSize;
        this.dateDownloaded = dateDownloaded;
    }

    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public String getCategory() { return category; }
    public long getFileSize() { return fileSize; }
    public long getDateDownloaded() { return dateDownloaded; }
}