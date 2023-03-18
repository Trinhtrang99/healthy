package com.example.healthy;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.wear.ambient.AmbientModeSupport;

import com.example.healthy.databinding.ActivityStepCountBinding;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;

public class StepCount extends Activity implements SensorEventListener, AmbientModeSupport.AmbientCallbackProvider,
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {
    private TextView mTextView;
    private ActivityStepCountBinding binding;
    private String msg = "0";
    SensorManager mSensorManager;
    boolean isEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStepCountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvStep.setText("0/" + getIntent().getStringExtra("COUNTSTEP") + "\n Bước");
        binding.btn.setOnClickListener(v -> {
            if (!isEnable) {
                binding.btn.setText("Dừng");
            } else {
               // mSensorManager.unregisterListener(this);
                binding.btn.setText("Bắt đầu");
                StartWearableActivityTask a = new StartWearableActivityTask(msg);
                a.execute();
            }
            getStepCount();

        });
    }

    private void getStepCount() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor mStepDetectSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mStepDetectSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            msg = " " + (int) event.values[0];
            StartWearableActivityTask a = new StartWearableActivityTask(msg);
            a.execute();
        }


    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        final String key;

        public StartWearableActivityTask(String msg) {
            key = msg;
        }


        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = UtriNotes.getInstance().getNodes(StepCount.this);
            for (String node : nodes) {
                UtriNotes.getInstance().sendStartActivityMessage(node, key, StepCount.this);
            }
            return null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}