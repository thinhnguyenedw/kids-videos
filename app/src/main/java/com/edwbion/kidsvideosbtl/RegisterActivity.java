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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Thay thế bằng layout của bạn

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        TextView loginTextView = findViewById(R.id.loginTextView);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình đăng nhập
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Lưu thông tin người dùng vào Firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("role", "user"); // Vai trò mặc định là "user"

                                    firestore.collection("users").document(userId)
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                                        // Chuyển đến màn hình đăng nhập
                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Lưu thông tin thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}