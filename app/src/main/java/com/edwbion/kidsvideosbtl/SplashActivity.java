package com.edwbion.kidsvideosbtl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  // Nếu bạn có layout cho splash, có thể thêm vào đây

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Sử dụng Handler để delay chuyển đến màn hình tiếp theo sau một khoảng thời gian ngắn (Splash screen)
        new Handler().postDelayed(() -> {
            if (currentUser != null) {
                // Nếu người dùng đã đăng nhập, chuyển sang MainScreen
                Intent intent = new Intent(SplashActivity.this, MainScreen.class);
                startActivity(intent);
            } else {
                // Nếu người dùng chưa đăng nhập, chuyển sang LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            // Kết thúc SplashActivity để người dùng không quay lại màn hình này
            finish();
        }, 2000); // Delay 2 giây (hoặc bất kỳ thời gian nào bạn muốn)
    }
}