package com.example.healthy.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.example.healthy.R;
import com.example.healthy.service.StepService;
import com.example.healthy.untils.Constants;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle bundle=intent.getBundleExtra(context.getString(R.string.bundle_alarm_obj));
        if (bundle!=null)
            startAlarmService(context);


    }

    private void startAlarmService(Context context) {
        Intent intentService = new Intent(context, StepService.class);
        intentService.setAction(Constants.START_FOREGROUND);
        context.startService(intentService);
    }
}