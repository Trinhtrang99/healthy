package com.example.healthy.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityLoginBinding;
import com.example.healthy.heart_rate.HeartRate;

public class ActivityLogin extends BaseActivity {
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
       // getAqi();


        binding.rootRegis.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityRegister.class));
            finish();
        });

        binding.btnLogin.setOnClickListener(view -> {

            startActivity(new Intent(this, HeartRate.class));
            finish();
            //   syncDataItem();
            //sendStartActivityMessage();
            //sendMessage();

        });
    }



}