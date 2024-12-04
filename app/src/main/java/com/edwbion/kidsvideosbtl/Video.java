package com.edwbion.kidsvideosbtl;

public class Video {
    private String title;
    private String description;
    private String videoUrl; // URL của video YouTube hoặc URL file video

    public Video() {
        // Constructor rỗng để Firestore có thể khởi tạo đối tượng
    }

    public Video(String title, String description, String videoUrl) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
