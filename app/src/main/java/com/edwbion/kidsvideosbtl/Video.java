package com.edwbion.kidsvideosbtl;

public class Video {
    private String title;
    private String description;
    private String videoUrl;

    public Video() {}

    public Video(String title, String description, String videoUrl) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
