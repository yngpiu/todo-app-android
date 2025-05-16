package com.haui.noteapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.haui.noteapp.databinding.ActivityLoginBinding;
import com.haui.noteapp.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Gọi API hoặc xử lý đăng nhập tại đây
            Toast.makeText(this, "Đăng nhập với: " + email, Toast.LENGTH_SHORT).show();
        });

        binding.signupTextView.setOnClickListener(v -> {
             startActivity(new Intent(this, SignupActivity.class));
        });
    }
}
