package com.example.healthy.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.example.healthy.ActivityInfoHealthy;
import com.example.healthy.R;
import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityRegisterBinding;

public class ActivityRegister extends BaseActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.btnCreate.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityInfoHealthy.class));
            finish();
        });
        binding.layotLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityLogin.class));
            finish();
        });
    }
}