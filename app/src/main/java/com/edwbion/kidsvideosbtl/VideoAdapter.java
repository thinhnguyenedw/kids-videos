package com.edwbion.kidsvideosbtl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videoList;
    private Context context;

    public VideoAdapter(List<Video> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        holder.videoTitle.setText(video.getTitle());
        holder.videoDescription.setText(video.getDescription());

        // Load thumbnail using Glide
        Glide.with(context)
                .load(getYouTubeThumbnailUrl(video.getVideoUrl()))
                .into(holder.videoThumbnail);

        // Xử lý sự kiện khi nhấn vào thumbnail để mở Activity phát video
        holder.videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Truyền video URL vào Intent
                String videoUrl = video.getVideoUrl(); // Đây là URL YouTube
                String description = video.getDescription();
                // Mở VideoPlayerActivity và truyền video URL
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("videoUrl", videoUrl);// Truyền video URL và description
                intent.putExtra("description", description);
                context.startActivity(intent);
            }
        });
    }

    private String getYouTubeThumbnailUrl(String videoUrl) {
        String videoId = extractVideoIdFromUrl(videoUrl);
        if (videoId != null) {
            // URL của thumbnail YouTube
            return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        }
        return null;
    }

    private String extractVideoIdFromUrl(String videoUrl) {
        String videoId = null;
        if (videoUrl != null && videoUrl.contains("v=")) {
            String[] parts = videoUrl.split("v=");
            if (parts.length > 1) {
                videoId = parts[1].split("&")[0];  // Lấy video ID, bỏ qua các tham số phụ
            }
        }
        return videoId;
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle, videoDescription;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDescription = itemView.findViewById(R.id.videoDescription);
        }
    }
}
