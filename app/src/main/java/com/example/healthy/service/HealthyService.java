package com.example.healthy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class HealthyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        DbHelper dbHelper = new DbHelper(getApplication());
//        dbHelper.addHealthy(new Healthy("abc","abc","abc",
//                "anv",
//                "anv",
//                "anv",
//                "anv",
//                "anv",
//                "anv"
//                ));

        return START_STICKY;
    }
}
