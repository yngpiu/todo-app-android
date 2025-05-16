package com.haui.noteapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.haui.noteapp.MainActivity;
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

        loginViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        loginViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.getLoadingLiveData().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loginButton.setEnabled(!isLoading);
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

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

            loginViewModel.login(email,password);
        });

        binding.signupTextView.setOnClickListener(v -> {
             startActivity(new Intent(this, SignupActivity.class));
        });
    }
}
