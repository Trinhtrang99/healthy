package com.example.healthy.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.healthy.ActivityTimeSleep;
import com.example.healthy.R;
import com.example.healthy.model.Steps;
import com.example.healthy.step.StepActivity;
import com.example.healthy.untils.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class StepService extends Service implements SensorEventListener {

    //Sensor related variables
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor stepCounter;


    //Variables used in calculations
    private long stepCount = 0;
    private long lastSteps = 0;
    private String compassOrientation;
    private double lastDistance = 0;
    private int prevStepCount = 0;
    private long stepTimestamp = 0;
    private long startTime = 0;
    long timeInMilliseconds = 0;
    long elapsedTime = 0;
    long updatedTime = 0;
    private int speed = 0;
    private double distance = 0;
    private float[] accelValues;
    private float[] magnetValues;
    private String timeString;
    private String elapsedString;


    private boolean isActive = false;

    private Handler handler = new Handler();
    String CHANNEL_ID = "swasthya";
    int notification_id = 1711101;
    String TAG = "service_error";
    // alarm
    Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    Uri ringtone;

    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        private StepService service;

        public MyBinder() {
            this.service = StepService.this;
        }

        public StepService getService() {
            return service;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        createNotificationChannnelAlarm();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //  ringtone = RingtoneManager.getActualDefaultRingtoneUri(this.getBaseContext(), RingtoneManager.TYPE_ALARM);
        //   startForeground(notification_id,getNotification("Starting Step Counter Service",""));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = context;
        String action = intent.getAction();
        switch (action) {

            case Constants.START_FOREGROUND:
                Log.d(TAG, "starting service");
                break;

            case Constants.RESET_COUNT:
                resetCount();
                break;

            case Constants.STOP_SAVE_COUNT:
                stopForegroundService(true);

            case Constants.STOP_FOREGROUND:
                Log.d(TAG, "stopping service");
//                stopForeground(true);
//                unregisterSensors();
//                handler.removeCallbacks(timerRunnable);
//                stopSelf();
                break;
            case Constants.STOP_ALARM:
                stopAlarm();
                stopSelf();
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.deleteNotificationChannel("alarm");
                vibrator.cancel();
                break;
            case Constants.STOP_REPEAT:
                Intent intentService = new Intent(getApplicationContext(), StepService.class);
                stopService(intentService);
                break;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopAlarm();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //Get sensor values
        switch (event.sensor.getType()) {
            case (Sensor.TYPE_ACCELEROMETER):
                accelValues = event.values;
                break;
            case (Sensor.TYPE_MAGNETIC_FIELD):
                magnetValues = event.values;
                break;
            case (Sensor.TYPE_STEP_COUNTER):
                if (prevStepCount < 1) {
                    prevStepCount = (int) event.values[0];
                }
                calculateSpeed(event.timestamp, (int) (event.values[0] - prevStepCount - stepCount));
                countSteps((int) (event.values[0] - prevStepCount - stepCount));
                break;
            case (Sensor.TYPE_STEP_DETECTOR):
                if (stepCounter == null) {
                    countSteps((int) event.values[0]);
                    calculateSpeed(event.timestamp, 1);
                }
                break;
        }

        if (accelValues != null && magnetValues != null) {
            float rotation[] = new float[9];
            float orientation[] = new float[3];
            if (SensorManager.getRotationMatrix(rotation, null, accelValues, magnetValues)) {
                SensorManager.getOrientation(rotation, orientation);
                float azimuthDegree = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
                float orientationDegree = Math.round(azimuthDegree);
                getOrientation(orientationDegree);
            }
        }
    }

    public long getStepCount() {
        return stepCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void startForegroundServiceWithAlarm(String s) {
        startForeground(notification_id, createNotificationAlarm(s));
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mediaPlayer.setDataSource(this.getBaseContext(), alarmUri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(mediaPlayer -> mediaPlayer.start());
    }

    public void startForegroundService() {
        registerSensors();
        startTime = SystemClock.uptimeMillis() + 1000;
        startForeground(notification_id, getNotification("Starting Step Counter Service", ""));
        handler.postDelayed(timerRunnable, 1000);
        isActive = true;
    }

    public void stopForegroundService(boolean persist) {
        unregisterSensors();
        handler.removeCallbacks(timerRunnable);
        isActive = false;
        startForeground(notification_id, getNotification("Stopping  Step Counter Service", ""));
        stopForeground(true);
        elapsedTime = elapsedTime + timeInMilliseconds;
        if (persist)
            persistSteps();
    }

    public void stopAlarm() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopForeground(true);
    }


    public void resetCount() {
        stepCount = 0;
        distance = 0;
        startTime = SystemClock.uptimeMillis();
        updatedTime = elapsedTime;
    }

    private void resetVariables() {

    }

    //Calculates the number of steps and the other calculations related to them
    private void countSteps(int step) {
        //Step count
        stepCount += step;

        //Distance calculation
        distance = stepCount * 0.8; //Average step length in an average adult
    }

    //Calculated the amount of steps taken per minute at the current rate
    private void calculateSpeed(long eventTimeStamp, int steps) {

        long timestampDifference = eventTimeStamp - stepTimestamp;
        stepTimestamp = eventTimeStamp;
        double stepTime = timestampDifference / 1000000000.0;
        speed = (int) (60 / stepTime);
    }

    //Show cardinal point (compass orientation) according to degree
    private void getOrientation(float orientationDegree) {

        if (orientationDegree >= 0 && orientationDegree < 90) {
            compassOrientation = "North";
        } else if (orientationDegree >= 90 && orientationDegree < 180) {
            compassOrientation = "East";
        } else if (orientationDegree >= 180 && orientationDegree < 270) {
            compassOrientation = "South";
        } else {
            compassOrientation = "West";
        }
    }

    //Runnable that calculates the elapsed time since the user presses the "start" button
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = elapsedTime + timeInMilliseconds;
            Notification notification = updateNoification();
            startForeground(notification_id, notification);
            Log.d(TAG, timeString);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors() {

        if (stepDetectorSensor != null)
            sensorManager.registerListener(StepService.this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        if (accelerometer != null)
            sensorManager.registerListener(StepService.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        if (magnetometer != null)
            sensorManager.registerListener(StepService.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (stepCounter != null)
            sensorManager.registerListener(StepService.this, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);

    }

    private void unregisterSensors() {

        if (stepDetectorSensor != null)
            sensorManager.unregisterListener(StepService.this, stepDetectorSensor);

        if (accelerometer != null)
            sensorManager.unregisterListener(StepService.this, accelerometer);

        if (magnetometer != null)
            sensorManager.unregisterListener(StepService.this, magnetometer);

        if (stepCounter != null)
            sensorManager.unregisterListener(StepService.this, stepCounter);

    }

    public HashMap<String, String> getPrevData() {

        HashMap<String, String> data = new HashMap<>();
        String distanceString = String.format("%.2f", lastDistance);

        data.put("duration", String.format(getString(R.string.time), elapsedString));
        data.put("steps", String.format(getString(R.string.steps1), lastSteps));
        data.put("distance", String.format(getString(R.string.distance), distanceString));
        data.put("speed", String.format(getResources().getString(R.string.speed), (int) (distance / (elapsedTime / (1000 * 60)))));
        return data;
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> data = new HashMap<>();
        String distanceString = String.format("%.2f", lastDistance + distance);

        int seconds = (int) (updatedTime / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;
        timeString = String.format("%d:%s:%s", hours, String.format("%02d", minutes), String.format("%02d", seconds));

        data.put("steps", String.format(getString(R.string.steps), lastSteps + stepCount));
        data.put("distance", String.format(getString(R.string.distance), distanceString));
        data.put("orientation", String.format(getString(R.string.orientation), compassOrientation));
        data.put("duration", String.format(getString(R.string.time), timeString));
        data.put("speed", String.format(getResources().getString(R.string.speed), speed));
        return data;
    }

    private Notification updateNoification() {

        String body = "";
        String title = "Step Counter ";
        HashMap<String, String> data = getData();

        body += data.get("distance") + "                ";
        body += data.get("duration");

        Notification notification = getNotification("Số bước chân :  " + data.get("steps"), body);

        return notification;
    }

    private void persistSteps() {

        try {
            final String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            Steps steps = new Steps(stepCount + lastSteps, elapsedTime, (stepCount + lastSteps) * 0.8, timestamp);
        } catch (Exception e) {

        }
    }

    private Notification getNotification(String title, String body) {

        Intent resetIntent = new Intent(this, StepService.class);
        resetIntent.setAction(Constants.RESET_COUNT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this, 0, resetIntent, 0);

        Intent stopIntent = new Intent(this, StepService.class);
        stopIntent.setAction(Constants.STOP_SAVE_COUNT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, resetIntent, 0);

        Intent intent = new Intent(this, StepActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo), 97, 128, false))
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .build();

        return notification;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "SWASTHYA",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotificationAlarm(String s) {

        Intent resetIntent = new Intent(this, StepService.class);
        resetIntent.setAction(Constants.STOP_REPEAT);
        PendingIntent resetPendingIntent = PendingIntent.getService(this, 11, resetIntent, 0);

        Intent stopIntent = new Intent(this, StepService.class);
        stopIntent.setAction(Constants.STOP_REPEAT);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 12, stopIntent, 0);

        Intent intent = new Intent(this, ActivityTimeSleep.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 14, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "alarm")
                .setContentTitle("Ring Ring .. Ring Ring")
                .setContentText(s)
                .setSmallIcon(R.drawable.ic_night)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.reset, "Ok", resetPendingIntent)
                .addAction(R.drawable.stop, "Báo Lại", stopPendingIntent)
                .setFullScreenIntent(resultPendingIntent, true)
                .build();


        return notification;
    }

    private void createNotificationChannnelAlarm() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "alarm",
                    getString(R.string.app_name) + "Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
