package com.edwbion.kidsvideosbtl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
        holder.videoThumbnail.setOnClickListener(v -> {
            // Lấy thông tin video
            String videoUrl = video.getVideoUrl();  // Đây là URL YouTube
            String title = video.getTitle();
            String description = video.getDescription();

            // Lấy userId của người dùng hiện tại
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Lưu video vào Firestore trong collection "watched"
            saveVideoToWatched(userId, videoUrl, title, description);
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("videoUrl", videoUrl); // Truyền video URL và description
            intent.putExtra("description", description);
            context.startActivity(intent);
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
    private void saveVideoToWatched(String userId, String videoUrl, String title, String description) {
        // Khởi tạo Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến collection "watched" của người dùng
        db.collection("users")
                .document(userId)
                .collection("watched")
                .add(new Video(title, description, videoUrl))  // Thêm video vào Firestore
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Video saved to watched!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving video: ", e);
                });
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
