package com.edwbion.kidsvideosbtl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, videoUrlEditText, ageRestrictionEditText;
    private Button submitButton;
    private ProgressBar progressBar;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // Layout của AdminActivity

        firestore = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        videoUrlEditText = findViewById(R.id.videoUrlEditText);
        ageRestrictionEditText = findViewById(R.id.ageRestrictionEditText);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String videoUrl = videoUrlEditText.getText().toString().trim();
                String ageRestrictionStr = ageRestrictionEditText.getText().toString().trim();

                // Kiểm tra các trường nhập liệu
                if (TextUtils.isEmpty(title)) {
                    titleEditText.setError("Vui lòng nhập tiêu đề");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    descriptionEditText.setError("Vui lòng nhập mô tả");
                    return;
                }

                if (TextUtils.isEmpty(videoUrl)) {
                    videoUrlEditText.setError("Vui lòng nhập URL video");
                    return;
                }

                // Kiểm tra định dạng URL video
                if (!videoUrl.startsWith("https://www.youtube.com/watch")) {
                    videoUrlEditText.setError("URL phải bắt đầu với https://www.youtube.com/watch");
                    return;
                }

                if (TextUtils.isEmpty(ageRestrictionStr)) {
                    ageRestrictionEditText.setError("Vui lòng nhập giới hạn độ tuổi");
                    return;
                }

                int ageRestriction;
                try {
                    ageRestriction = Integer.parseInt(ageRestrictionStr);
                } catch (NumberFormatException e) {
                    ageRestrictionEditText.setError("Giới hạn độ tuổi phải là số");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                submitButton.setEnabled(false);

                // Tạo dữ liệu video
                Map<String, Object> videoData = new HashMap<>();
                videoData.put("title", title);
                videoData.put("description", description);
                videoData.put("videoUrl", videoUrl);
                videoData.put("ageRestriction", ageRestriction);

                // Gửi dữ liệu lên Firestore
                firestore.collection("video")
                        .add(videoData)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            submitButton.setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(AdminActivity.this, "Đã thêm video thành công!", Toast.LENGTH_SHORT).show();

                                // Xóa các trường nhập liệu
                                titleEditText.setText("");
                                descriptionEditText.setText("");
                                videoUrlEditText.setText("");
                                ageRestrictionEditText.setText("");
                            } else {
                                Toast.makeText(AdminActivity.this, "Lỗi khi thêm video: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}