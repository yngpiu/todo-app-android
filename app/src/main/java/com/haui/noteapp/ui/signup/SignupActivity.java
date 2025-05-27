package com.haui.noteapp.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.haui.noteapp.ui.MainActivity;
import com.haui.noteapp.databinding.ActivitySignupBinding;
import com.haui.noteapp.model.User;
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

        initObservers();
        setupListeners();
    }

    private void initObservers() {
        signupViewModel.getSignUpSuccess().observe(this, success -> {
            binding.progressBar.setVisibility(View.GONE);
            if (success) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        signupViewModel.getErrorMessage().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        signupViewModel.getActionMessage().observe(this, event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.registerButton.setOnClickListener(v -> {
            String email = String.valueOf(binding.emailEditText.getText()).trim();
            String password = String.valueOf(binding.passwordEditText.getText()).trim();
            String confirmPassword = String.valueOf(binding.confirmPasswordEditText.getText()).trim();
            String displayName = String.valueOf(binding.displayNameEditText.getText()).trim();


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
                binding.confirmPasswordInputLayout.setError("Vui lòng xác nhận mật khẩu");
                isValid = false;
            } else if (!password.equals(confirmPassword)) {
                binding.confirmPasswordInputLayout.setError("Mật khẩu xác nhận không khớp");
                isValid = false;
            } else {
                binding.confirmPasswordInputLayout.setError(null);
            }

            if (!isValid) return;

            binding.progressBar.setVisibility(View.VISIBLE);

            User user = new User();
            user.setDisplayName(displayName);

            signupViewModel.signUp(email, password, user);
        });

        binding.loginTextView.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
