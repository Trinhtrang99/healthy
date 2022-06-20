package com.example.wear;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wear.databinding.ActivityDetailBinding;

public class Detail extends Activity implements SensorEventListener {

    private TextView mTextView;
    private ActivityDetailBinding binding;
    private int type;
    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getIntent() != null) {
            type = getIntent().getIntExtra("type", -1);
            if (type == 2) {
                binding.appName.setText("Huyết áp");
                binding.heart.setImageResource(R.drawable.huyetap);

            }
        }
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (mHeartRateSensor == null) {

            Toast.makeText(this, "Device không hỗ trợ", Toast.LENGTH_SHORT).show();

        }

        binding.imgUpdate.setOnClickListener(view -> {
            binding.lastMeasuredValue.setVisibility(View.GONE);
            binding.progress.setVisibility(view.VISIBLE);
            new Handler().postDelayed(() -> {
                binding.lastMeasuredValue.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(view.GONE);
            }, 3000);
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Toast.makeText(this, "hihi "+String.valueOf(sensorEvent.values[0]), Toast.LENGTH_SHORT).show();
        String a = sensorEvent.values[0]+"";
        Log.d("hahaha", String.valueOf(sensorEvent.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}