package com.haui.noteapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.haui.noteapp.ui.MainActivity;
import com.haui.noteapp.databinding.ActivityLoginBinding;
import com.haui.noteapp.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        initObservers();
        setupListeners();
    }

    private void initObservers() {
        loginViewModel.getLoginSuccess().observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        loginViewModel.getErrorMessage().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.getActionMessage().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String email = String.valueOf(binding.emailEditText.getText()).trim();
            String password = String.valueOf(binding.passwordEditText.getText()).trim();

            boolean isValid = true;

            if (email.isEmpty()) {
                binding.emailInputLayout.setError("Vui lòng nhập email");
                isValid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInputLayout.setError("Email không hợp lệ");
                isValid = false;
            } else {
                binding.emailInputLayout.setError(null);
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.setError("Vui lòng nhập mật khẩu");
                isValid = false;
            } else {
                binding.passwordInputLayout.setError(null);
            }

            if (!isValid) return;

            binding.progressBar.setVisibility(View.VISIBLE);

            loginViewModel.login(email, password);
        });

        binding.signupTextView.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }
}
