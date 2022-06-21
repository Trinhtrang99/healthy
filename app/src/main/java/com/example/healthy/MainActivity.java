package com.example.healthy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityMainBinding;
import com.example.healthy.fragment.DiaryFragment;
import com.example.healthy.fragment.EventFragment;
import com.example.healthy.fragment.HomeFragment;
import com.example.healthy.fragment.ProfileFragment;
import com.example.healthy.network.ApiUtils;
import com.example.healthy.network.response.AqiResponse;
import com.example.healthy.service.StepService;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;
import com.example.healthy.untils.GpsTracker;
import com.example.healthy.untils.NotificationPublisher;
import com.frogobox.notification.FrogoNotification;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private static final String TAG = "kmfg";
    private ActivityMainBinding binding;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private EventFragment eventFragment;
    private DiaryFragment diaryFragment;
    private GpsTracker gpsTracker;
    FusedLocationProviderClient mFuse;
    DbHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Intent startIntent = new Intent(this, StepService.class);
        startIntent.setAction(Constants.START_FOREGROUND);
        startService(startIntent);

//        Intent intent = new Intent(this, HealthyService.class);
//        startService(intent);


        //  Log.d("kmfg", dbHelper.getHealthy().toString());

        getLocation();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getLocation();
            }
        };
        long delay = 60000 * 5;
        Timer timer = new Timer("Timer");
        timer.schedule(timerTask, 0, delay);


        initFragment();
        replaceFragment(homeFragment, homeFragment.getTag());
        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_ai_scope:
                    replaceFragment(homeFragment, homeFragment.getTag());
                    break;
                case R.id.navigation_diary:
                    replaceFragment(diaryFragment, diaryFragment.getTag());
                    break;
                case R.id.navigation_inspection_results_top:
                    replaceFragment(eventFragment, eventFragment.getTag());
                    break;
                case R.id.navigation_configuration:

                    replaceFragment(profileFragment, profileFragment.getTag());
                    break;

            }
            return true;
        });
        //createChanel();

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 11);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getLocation() {
        gpsTracker = new GpsTracker(this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            getAqi(latitude, longitude);
            Log.d("kmfg", latitude + "  " + longitude);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFragment() {
        homeFragment = new HomeFragment();
        diaryFragment = new DiaryFragment();
        profileFragment = new ProfileFragment();
        eventFragment = new EventFragment();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    private void getAqi(Double lat, Double lng) {
        ApiUtils.getApiServiceEnv().getAqi(lat, lng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AqiResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull AqiResponse aqiResponse) {
                        Log.d("kmfg", aqiResponse.data.aqi + "");
                        int aqi = aqiResponse.data.aqi;
                        //0-50 good
                        //51 -100 Moderate
                        // 101-150 Không lành mạnh cho các nhóm nhạy cảm
//                        151-200 không khỏe mạnh
//                        201-300 Very Unhealthy
//                        300+ nguy hiểm

                        if (0 < aqi && aqi <= 50) {

                            pushNotificationWranning(" Mức độ không khí an toàn");
                        } else if (51 <= aqi && aqi <= 100) {

                            pushNotificationWranning(" Mức độ không khí trung bình");
                        } else if (101 <= aqi && aqi <= 150) {

                            pushNotificationWranning(" Mức độ không khí nguy hiểm với nhóm nhậy cảm ");
                        } else if (151 <= aqi && aqi <= 200) {
                            pushNotificationWranning(" Mức độ không khí không an toàn ");

                        } else if (201 <= aqi && aqi <= 300) {

                            pushNotificationWranning(" Mức độ không khí nguy hiểm");
                        } else if (aqi > 300) {
                            pushNotificationWranning("Mức độ không khí rất nguy hiểm");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("kmfg", e.getLocalizedMessage() + " " + e.getMessage());
                    }
                });
    }

    private void pushNotificationWranning(String text) {
        new Handler().postDelayed(() -> {
            new FrogoNotification.Inject(MainActivity.this)
                    .setChannelId("Channel2")
                    .setChannelName("Channel2Noti")
                    .setContentText(text)
                    .setContentTitle("Cảnh báo không khí")
                    .build().launch(1000);
        }, 10000);
    }

    public String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        return formattedDate;

    }


}