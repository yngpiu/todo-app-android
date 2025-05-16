package com.haui.noteapp.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.haui.noteapp.MainActivity;
import com.haui.noteapp.databinding.ActivitySignupBinding;
import com.haui.noteapp.ui.login.LoginActivity;


public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);


        signupViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        signupViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        signupViewModel.getLoadingLiveData().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.registerButton.setEnabled(!isLoading);
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.registerButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
            String displayName = binding.displayNameEditText.getText().toString().trim();

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

            if (displayName.isEmpty()) {
                binding.displayNameInputLayout.setError("Vui lòng nhập tên hiển thị");
                isValid = false;
            } else if (displayName.length() < 2) {
                binding.displayNameInputLayout.setError("Tên hiển thị phải có ít nhất 2 ký tự");
                isValid = false;
            } else {
                binding.displayNameInputLayout.setError(null);
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.setError("Vui lòng nhập mật khẩu");
                isValid = false;
            } else if (password.length() < 6) {
                binding.passwordInputLayout.setError("Mật khẩu phải có ít nhất 6 ký tự");
                isValid = false;
            } else {
                binding.passwordInputLayout.setError(null);
            }

            if (confirmPassword.isEmpty()) {
                binding.confirmPasswordInputLayout.setError("Vui lòng nhập xác nhận mật khẩu");
                isValid = false;
            } else if (!password.equals(confirmPassword)) {
                binding.confirmPasswordInputLayout.setError("Mật khẩu xác nhận không khớp");
                isValid = false;
            } else {
                binding.confirmPasswordInputLayout.setError(null);
            }

            if (!isValid) return;

            signupViewModel.signup(email, password, displayName);

        });

        binding.loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }


}
