package com.example.healthy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {
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
        dbHelper = new DbHelper(this);

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


    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        final String key;

        public StartWearableActivityTask(String msg) {
            key = msg;
        }


        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node, key);
            }
            return null;
        }
    }

    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes) {
                results.add(node.getId());
            }
        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);
        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
        return results;
    }

    @WorkerThread
    private void sendStartActivityMessage(String node, String event) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, "/APP_OPEN_WEARABLE_PAYLOAD", event.getBytes());

        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            Integer result = Tasks.await(sendMessageTask);

        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public void onCapabilityChanged(@androidx.annotation.NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@androidx.annotation.NonNull DataEventBuffer dataEventBuffer) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@androidx.annotation.NonNull MessageEvent messageEvent) {
        String rate = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        if (rate.isEmpty()) {
            rate = "0";
        }
        dbHelper.updateHealthy(Constants.HEART, rate, dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).id);
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.frame_container);
        if (f instanceof HomeFragment) {
            ((HomeFragment) f).onResume();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient(this).addListener(this);
            Wearable.getMessageClient(this).addListener(this);
            Wearable.getCapabilityClient(this)
                    .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, StepService.class);

    }


    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }
}