package com.edwbion.kidsvideosbtl;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class VideoPlayer extends AppCompatActivity implements SensorEventListener {

    private WebView webView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity = null;
    private float[] geomagnetic = null;
    TextView videoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Nhận video URL từ Intent
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String videoDescription = getIntent().getStringExtra("description");
        // Khởi tạo WebView
        webView = findViewById(R.id.webView);
        videoInfo=findViewById(R.id.videoInfo);




        // Cấu hình WebView
        webView.getSettings().setJavaScriptEnabled(true);
        // Bật JavaScript
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());  // Đảm bảo liên kết được mở trong WebView

        // Tạo URL cho video YouTube

        String youtubeEmbedUrl = convertYouTubeUrlToEmbed(videoUrl);
        // Tải video vào WebView
        webView.loadUrl(youtubeEmbedUrl);
        //Lấy description
        videoInfo.setText(videoDescription);
        // Khởi tạo cảm biến
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    // Hàm để chuyển đổi URL YouTube sang URL nhúng
    private String convertYouTubeUrlToEmbed(String videoUrl) {
        String videoId = extractVideoIdFromUrl(videoUrl);
        if (videoId != null) {
            // Sử dụng các tham số để tự động phát video và ẩn nút toàn màn hình
            return "https://www.youtube.com/embed/" + videoId
                    + "?autoplay=1&fs=0&modestbranding=1&rel=0&showinfo=0&controls=1";
        }
        return null;
    }
    // Hàm để trích xuất video ID từ URL YouTube
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
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();  // Tạm dừng WebView
        }
        // Chỉ gọi exitLandscapeMode() nếu cần
        // Không cần phải gọi ở đây vì exitLandscapeMode() chỉ cần được gọi khi chuyển về chế độ portrait
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();  // Tiếp tục WebView
        }

        // Kiểm tra nếu màn hình không phải landscape
        if (isLandscape()) {
            // Nếu ở chế độ landscape, không cần phải gọi exitLandscapeMode()
            // Để tránh việc hiển thị lại TextView khi không cần thiết
        } else {
            // Nếu đang ở chế độ portrait, khôi phục lại bình thường
            exitLandscapeMode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();  // Giải phóng tài nguyên WebView
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Xác định loại cảm biến và gán giá trị cho mảng gravity hoặc geomagnetic
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone(); // Clone để tránh dữ liệu bị thay đổi ngoài ý muốn
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }

        // Nếu đã nhận đủ dữ liệu từ cả hai cảm biến
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);

            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                float pitch = orientation[1]; // Độ nghiêng theo trục Y
                float roll = orientation[2];  // Độ nghiêng theo trục Z

                // Kiểm tra góc nghiêng để chuyển đổi chế độ
                if (Math.abs(pitch) > 1.5 || Math.abs(roll) > 1.5) {
                    enterLandscapeMode(); // Chuyển sang chế độ landscape
                } else {
                    exitLandscapeMode(); // Quay lại chế độ portrait
                }
            }
        }
    }

    // Hàm để chuyển sang chế độ landscape
    private void enterLandscapeMode() {
        ConstraintLayout constraintLayout = findViewById(R.id.main);
        // Ẩn thanh trạng thái và navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Ẩn TextView
        videoInfo.setVisibility(View.GONE);

        // WebView chiếm toàn màn hình
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) webView.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT;

        // Cập nhật lại layoutParams cho WebView
        webView.setLayoutParams(layoutParams);
    }

    // Hàm để quay lại chế độ portrait
    private void exitLandscapeMode() {
        // Hiển thị lại thanh trạng thái
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Hiển thị TextView
        videoInfo.setVisibility(View.VISIBLE);

        // WebView quay lại chiều cao
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) webView.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = 0; // Dựa trên ràng buộc tỷ lệ
        webView.setLayoutParams(layoutParams);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private boolean isLandscape() {
        // Kiểm tra xem hiện tại có phải là chế độ landscape không
        return getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        }
    }

