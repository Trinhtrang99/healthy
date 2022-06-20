package com.example.healthy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.databinding.ActivitySplashBinding;
import com.example.healthy.sqlite.DbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        dbHelper = new DbHelper(this);
        dbHelper.createDataBase();
        dbHelper.openDataBase();
        if (dbHelper.getInformation().birthday != null) {
            new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this,
                    MainActivity.class)), 5000);
        } else {
            new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this,
                    ActivityInfoHealthy.class)), 1000);
        }


    }

}