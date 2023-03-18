package com.example.healthy;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.ambient.AmbientModeSupport;

import com.example.healthy.databinding.ActivityDetailBinding;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class Detail extends AppCompatActivity implements SensorEventListener, AmbientModeSupport.AmbientCallbackProvider,
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private TextView mTextView;
    private ActivityDetailBinding binding;
    private int type;
    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;
    AmbientModeSupport.AmbientController ambientController;
    String msg = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ambientController = AmbientModeSupport.attach(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String heart = getIntent().getStringExtra("HEART");
        String[] res = heart.split("d");
        if (res.length == 2) {
            binding.lastMeasuredValue.setText(res[0]);
            binding.lastMeasuredLabel.setText("last_measured_label: " + res[1]);
        }

        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", -1);
            if (type == 2) {
                binding.appName.setText("Huyết áp");
                binding.heart.setImageResource(R.drawable.huyetap);

            }
        }
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        binding.imgUpdate.setOnClickListener(view -> {
//
            LocalDateTime current = LocalDateTime.now();
            String formatted = current.format(formatter);
            getStepCount();
            binding.lastMeasuredValue.setVisibility(View.GONE);
            binding.progress.setVisibility(view.VISIBLE);
            new Handler().postDelayed(() -> {
                binding.lastMeasuredLabel.setText("last_measured_label: " + formatted);
                binding.lastMeasuredValue.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(view.GONE);
                binding.lastMeasuredValue.setText("0");
                StartWearableActivityTask a = new StartWearableActivityTask(binding.lastMeasuredValue.getText().toString());
                a.execute();
            }, 3000);
        });

    }

    private void getStepCount() {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            msg = "" + (int) event.values[0];

        } /*else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String msg = "" + (int) event.values[0];

        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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


    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        final String key;

        public StartWearableActivityTask(String msg) {
            key = msg;
        }


        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = UtriNotes.getInstance().getNodes(Detail.this);
            for (String node : nodes) {
                UtriNotes.getInstance().sendStartActivityMessage(node, key, Detail.this);
            }
            return null;
        }
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
        mSensorManager.unregisterListener(this);
    }
}