package com.example.wear;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.health.services.client.HealthServices;
import androidx.health.services.client.HealthServicesClient;
import androidx.health.services.client.PassiveMonitoringClient;
import androidx.health.services.client.data.DataType;
import androidx.health.services.client.data.PassiveMonitoringCapabilities;

import com.example.wear.databinding.ActivityMainBinding;
import com.example.wear.lib.TeleportClient;
import com.google.android.gms.wearable.DataMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CountDownLatch;

public class MainActivity extends Activity implements SensorEventListener {

    private RelativeLayout btnHeart;
    private ActivityMainBinding binding;

    private static final String TAG = "MY_TASK";

    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;
    private CountDownLatch latch;
    TeleportClient mTeleportClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnHeart = binding.btnHeart;
        btnHeart.setOnClickListener(view -> {
            Intent i = new Intent(this, Detail.class);
            i.putExtra("type", 1);

            startActivity(i);
        });
        binding.btnBlood.setOnClickListener(view -> {
            Intent i = new Intent(this, Detail.class);
            i.putExtra("type", 2);

            startActivity(i);
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (keep) {
                //startactivity only code goes here
            }
        }
        //instantiate the TeleportClient with the application Context
        mTeleportClient = new TeleportClient(this);

        mTeleportClient.setOnSyncDataItemTask(new ShowToastHelloWorldTask());


        //let's set the two task to be executed when an item is synced or a message is received
        mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
        latch = new CountDownLatch(1);
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE); // using Sensor Lib2 (Samsung Gear Live)
        //mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (mHeartRateSensor == null)
            Log.d(TAG, "heart rate sensor is null");


        HealthServicesClient healthClient = HealthServices.getClient(this /*context*/);
        PassiveMonitoringClient passiveClient = healthClient.getPassiveMonitoringClient();

        ListenableFuture<PassiveMonitoringCapabilities> capabilitiesFuture = passiveClient.getCapabilities();
        Futures.addCallback(capabilitiesFuture,
                new FutureCallback<PassiveMonitoringCapabilities>() {
                    @Override
                    public void onSuccess(@Nullable PassiveMonitoringCapabilities result) {
                        Boolean supportsHeartRate = result
                                .getSupportedDataTypesPassiveMonitoring()
                                .contains(DataType.HEART_RATE_BPM);
                        Boolean supportsStepsEvent = result
                                .getSupportedDataTypesEvents()
                                .contains(DataType.FLAT_GROUND_DURATION);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // display an error
                    }
                },
                ContextCompat.getMainExecutor(this /*context*/));

        int running_step = DataType.RUNNING_STEPS.getFormat();
        int heart_rate_bpm = DataType.HEART_RATE_BPM.getFormat();
        int spo2Format = DataType.SPO2.getFormat();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            latch.await();
            if (sensorEvent.values[0] > 0) {
                mTeleportClient.sendMessage(String.valueOf(sensorEvent.values[0]), null);
                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);

            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "accuracy changed: " + i);
        // accuracy.setText("Accuracy: " + Integer.toString(i));
    }

    //Task that shows the path of a received message
    public class ShowToastFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {

        @Override
        protected void onPostExecute(String path) {

            //Toast.makeText(getApplicationContext(), "Message - " + path, Toast.LENGTH_SHORT).show();
            //let's reset the task (otherwise it will be executed only once)
            mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTeleportClient != null)
            mTeleportClient.disconnect();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (!keep) {
                finish();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mTeleportClient != null)
            mTeleportClient.connect();
        mSensorManager.registerListener(this, this.mHeartRateSensor, 3);
    }

    public class ShowToastHelloWorldTask extends TeleportClient.OnSyncDataItemTask {

        @Override
        protected void onPostExecute(DataMap dataMap) {

            String hello = dataMap.getString("hello");
            Log.d("kmdf", hello);
            //  tv.setText(hello);
            //Toast.makeText(getApplicationContext(), hello, Toast.LENGTH_SHORT).show();
        }
    }

}