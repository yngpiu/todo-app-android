package com.haui.noteapp.ui.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.haui.noteapp.databinding.ActivitySettingBinding;
import com.haui.noteapp.ui.login.LoginActivity;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private SettingViewModel settingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        MaterialToolbar toolbar = binding.toolbar;
        toolbar.setTitle("Cài đặt");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        settingViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this).setTitle("Xác nhận đăng xuất").setMessage("Bạn có chắc chắn muốn đăng xuất không?").setPositiveButton("Đăng xuất", (dialog, which) -> {
                settingViewModel.signOut();
            }).setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()).show();
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}