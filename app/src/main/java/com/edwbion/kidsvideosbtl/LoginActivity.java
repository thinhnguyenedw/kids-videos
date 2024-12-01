package com.edwbion.kidsvideosbtl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Kiểm tra trạng thái đăng nhập trước khi hiển thị màn hình đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, kiểm tra quyền và chuyển màn hình
            checkUserRole(currentUser.getUid());
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        TextView registerTextView = findViewById(R.id.registerTextView);

        registerTextView.setOnClickListener(v -> {
            // Chuyển đến màn hình đăng ký
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(LoginActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            // Kiểm tra quyền trong Firestore
                            checkUserRole(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if ("admin".equals(role)) {
                            // Chuyển đến AdminActivity
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        } else {
                            // Chuyển đến MainScreen
                            startActivity(new Intent(LoginActivity.this, MainScreen.class));
                        }

                        finish(); // Kết thúc LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Lỗi khi kiểm tra quyền: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
