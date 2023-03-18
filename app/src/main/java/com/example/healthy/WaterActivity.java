package com.example.healthy;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.adapter.AdaterWater;
import com.example.healthy.databinding.ActivityWaterBinding;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WaterActivity extends AppCompatActivity {
    ActivityWaterBinding binding;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Calendar cal = Calendar.getInstance();
    List<String> list;
    float mWater = 0;
    float ratio = 0;
    float tong = 0;
    AdaterWater adaterWater;
    DbHelper dbHelper;
    private int position;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_water);
        dbHelper = new DbHelper(this);
        dbHelper.createDataBase();
        position = dbHelper.getHealthy().size() - 1;
        binding.waveLoadingView.setCenterTitle(dbHelper.getHealthy().get(position).water + " ml");
        list = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        if (!dbHelper.getHealthy().get(position).date.equals(dtf.format(now))) {
            dbHelper.deleteWater();
        }
        if (dbHelper.getWater().size() > 0) {
            for (int i = 0; i < dbHelper.getWater().size(); i++) {
                list.add(dbHelper.getWater().get(i).time);
            }
        }
        adaterWater = new AdaterWater(list);
        binding.rc.setAdapter(adaterWater);
        mWater = 2000;
        ratio = 100 / mWater;
        binding.imgWater.setOnClickListener(view -> {
            if (tong > mWater) {
                Toast.makeText(this, "Bạn đã hoàn thành lượng nước cần thiết ngày hôm nay", Toast.LENGTH_SHORT).show();
            } else {
                tong += 200;
                int value = (int) (tong * ratio);
                binding.waveLoadingView.setProgressValue(value);
                list.add(dateFormat.format(cal.getTime()));
                binding.waveLoadingView.setCenterTitle(tong + " ml");
                adaterWater.updateAdapter(list);
                dbHelper.addWater(dateFormat.format(cal.getTime()));
                int water = 0;
                for (int i = 0; i < list.size(); i++) {
                    water = water + 200;
                }
                dbHelper.updateHealthy(Constants.WATER, water + "", dbHelper.getHealthy().get(position).id);
            }

        });
        binding.imgback.setOnClickListener(view -> finish());
    }
}