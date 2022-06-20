package com.example.healthy.step;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.databinding.ActivityStepBinding;
import com.example.healthy.model.StepModel;
import com.example.healthy.service.StepService;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class StepActivity extends AppCompatActivity {

    private StepService service = null;
    private boolean isBound;

    private Handler handler = new Handler();
    private ActivityStepBinding binding;
    private int step = 1000;
    private DbHelper dbHelper;
    private String gender;
    private float weight, height;
    private int old;
    private int position;

    private Double RMR = 0.0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_step);
        dbHelper = new DbHelper(this);
        binding.imgBack.setOnClickListener(view -> {
            finish();
        });
        gender = dbHelper.getInformation().gender;
        weight = Float.parseFloat(dbHelper.getInformation().weight.replace("kg", "").trim());
        height = Float.parseFloat(dbHelper.getInformation().height.replace("cm", "").trim());
        position = dbHelper.getHealthy().size() - 1;
        DateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = (Date) parser.parse(dbHelper.getInformation().birthday);
            old = 2022 - date.getYear();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        RMR = cacularWithOcc(dbHelper.getInformation().occu, cacularRMR(gender, weight, height, old));

        binding.waveLoadingView.setTopTitle("Mục tiêu : " + Math.round(RMR) * 20);
        binding.waveLoadingView.setProgressValue(0);

        binding.tvStep.setText(dbHelper.getSteps().getSteps() + "\n Bước");
        binding.tvCalo.setText(dbHelper.getSteps().getKcal() + "\n Kcal");
        binding.tvTime.setText(dbHelper.getSteps().getDistance() + "\n m");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        if (!dbHelper.getSteps().getDate().equals(dtf.format(now))) {
            dbHelper.addStep(new StepModel("0", "0", "0",
                    dtf.format(now)
            ));
        }

        if (!checkSensors()) {
            binding.btnRun.setEnabled(false);
        }

        binding.btnRun.setOnClickListener(view -> {

            if (!isBound) {
                binding.tvMess.setText("Đang kết nối tới sevice đếm bước,....");
            } else if (!service.isActive()) {
                binding.tvStatus.setText("Dừng");
                binding.tvMess.setText("Cảm biến có độ trễ là 10s....");
                service.startForegroundService();
                binding.imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_24));

            } else {
                service.stopForegroundService(true);
                binding.tvStatus.setText("Bắt đầu");
                binding.imgStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                checkSensors();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean checkSensors() {

        SensorManager sensorManager;
        Sensor stepDetectorSensor;
        Sensor stepCounter;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //phát hiện bc chân
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounter != null) {
            return true;
        } else if (stepDetectorSensor != null) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        handler.postDelayed(timerRunnable, 0);

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder_service) {
            StepService.MyBinder myBinder = (StepService.MyBinder) binder_service;
            service = myBinder.getService();
            isBound = true;
        }
    };
    private Runnable timerRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            HashMap<String, String> data;

            if (isBound) {

                data = service.getData();
                dbHelper.updateHealthy(Constants.STEPS, data.get("steps").toString(), dbHelper.getHealthy().get(position).id);
                dbHelper.updateHealthy(Constants.CALORIES, Integer.parseInt(data.get("steps")) / 20 + "", dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).id);
                binding.tvStep.setText(data.get("steps") + "\n Bước");
                binding.waveLoadingView.setCenterTitle(data.get("steps"));
                binding.tvCalo
                        .setText(Integer
                                .parseInt(
                                        (Integer.parseInt(Objects.requireNonNull(data.get("steps"))) / 20) + "") + "\n Kal");
                binding.tvTime.setText(data.get("distance") + "\n m");
                int value = Integer.parseInt((100 / 1000) * Integer.parseInt(data.get("steps")) + "");
                binding.waveLoadingView.setProgressValue(value);
                if (service.isActive()) {
                    binding.tvStatus.setText("Dừng");

                } else {
                    binding.tvStatus.setText("Bắt đầu");

                }
                dbHelper.updateSteps(new StepModel(data.get("steps")
                        , (Integer.parseInt(Objects.requireNonNull(data.get("steps"))) / 20) + "", data.get("distance")
                        , "date"), dbHelper.getSteps().getId());

            }


//
            handler.postDelayed(this, 1000);
        }
    };

    public Double cacularRMR(String gender, float kg, float cm, int old) {
        double RMR = 0;
        if (gender.equals(getString(R.string.nu))) {
            RMR = 9.99 * kg + 6.25 * cm - 4.92 * old - 161;
        } else {
            RMR = 9.99 * kg + 6.25 * cm - 4.92 * old + 5;
        }
        return RMR;
    }

    public Double cacularWithOcc(String occu, Double RMR) {

        if (occu.equals(getString(R.string.it_vd))) {
            return RMR * 1.2;
        } else if (occu.equals(getString(R.string.vd_nhe))) {
            return RMR * 1.375;

        } else if (occu.equals(getString(R.string.vd_vua_phai))) {
            return RMR * 1.55;

        } else if (occu.equals(getString(R.string.vd_nhieu))) {
            return RMR * 1.725;

        } else if (occu.equals(getString(R.string.vd_cao))) {
            return RMR * 1.9;

        }
        return RMR;

    }
}