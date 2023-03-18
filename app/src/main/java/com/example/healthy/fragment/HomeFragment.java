package com.example.healthy.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.healthy.ActivityTimeSleep;
import com.example.healthy.R;
import com.example.healthy.WaterActivity;
import com.example.healthy.adapter.AdapterInfo;
import com.example.healthy.databinding.FragmentHomeBinding;
import com.example.healthy.model.Healthy;
import com.example.healthy.model.Information;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.step.StepActivity;
import com.example.healthy.untils.SharedUtils;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {
    public FragmentHomeBinding binding;
    private AdapterInfo adapterInfo;
    private GridLayoutManager manager;
    private DbHelper dbHelper;
    private double BMI;
    private List<Information> list;
    private Double BMITEMP;
    private String gender;
    private float weight, height;
    private int old;
    private Double RMR = 0.0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        dbHelper = new DbHelper(requireContext());

        adapterInfo = new AdapterInfo(list, getContext());
        manager = new GridLayoutManager(getContext(), 3);
        binding.rc.setAdapter(adapterInfo);
        binding.rc.setLayoutManager(manager);

        binding.crRun.setOnClickListener(view -> {

            Intent i = new Intent(getActivity(), StepActivity.class);
            startActivity(i);
        });
        binding.crWater.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), WaterActivity.class);
            startActivity(i);
        });
        binding.crSleep.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ActivityTimeSleep.class);
            startActivity(i);
        });

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient(getActivity()).addListener(this);
            Wearable.getMessageClient(getActivity()).addListener(this);
            Wearable.getCapabilityClient(getActivity())
                    .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Float height = Float.parseFloat(dbHelper.getInformation().height.replace("cm", "").trim()) / 100;
        Float weight = Float.parseFloat(dbHelper.getInformation().weight.replace("kg", "").trim());
        dbHelper.createDataBase();
        list = new ArrayList<>();
        BMI = weight / Math.pow(height, 2);

        BMITEMP = (double) Math.round(BMI * 10) / 10;
        SharedUtils.saveString(requireContext(), "BMI", BMITEMP.toString());
        if (BMITEMP < 18.5) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " bạn đang thiếu cân");
        } else if (18.5 < BMITEMP && BMITEMP < 22.9) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " bạn đang có cân nặng đạt chuẩn");
        } else if (23 < BMITEMP && BMITEMP < 24.9) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " bạn đang tăng cân");
        } else if (25 < BMITEMP && BMITEMP < 29) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " cảnh báo béo phì mức độ 1");
        } else if (30 < BMITEMP && BMITEMP < 39.9) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " cảnh báo béo phì mức độ 2");
        } else if (40 < BMITEMP) {
            binding.Rate.setText("Bạn đang có chỉ số BMI là " + BMITEMP + " cảnh báo béo phì mức độ 3");
        }

        binding.crSleep.setPercent(0.0F);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        if (dbHelper.getHealthy().size() == 0 ||
                !dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).date.equals(dtf.format(now))
        ) {
            dbHelper.addHealthy(new Healthy("0", "0", "0",
                    "0",
                    "0",
                    BMITEMP.toString(),
                    "0",
                    "0",
                    dtf.format(now), "0"
            ));
        }


        binding.tvStep.setText(dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).step + "\n bước");
        float persenStep = (Integer.parseInt(dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).step) / 1000) * 100;
        binding.crRun.setPercent(persenStep);
        binding.tvPersenStep.setText(persenStep + "%");


        float persenWater = (Integer.parseInt(dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).step) / 2000) * 100;
        binding.crWater.setPercent(persenWater);
        binding.tvPersenWater.setText(persenWater + "%");

        binding.tvSleep.setText(dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).sleep);


        int position = dbHelper.getHealthy().size() - 1;
        list.clear();
        list.add(new Information(R.color.red, R.drawable.heart, dbHelper.getHealthy().get(position).heart + " bpm", "Nhịp tim"));
        list.add(new Information(R.color.txt_color, R.drawable.huyetap, dbHelper.getHealthy().get(position).bool + "  mmHg", "Huyết áp"));
        list.add(new Information(R.color.main_color, R.drawable.ic_baseline_height_24, dbHelper.getInformation().height, "Chiều cao"));
        list.add(new Information(R.color.yellow, R.drawable.weight, dbHelper.getInformation().weight, "Cân nặng"));
        list.add(new Information(R.color.green_018577, R.drawable.bmi, BMITEMP + "", "BMI"));
        list.add(new Information(R.color.blue_2c3764, R.drawable.clipart1775369, dbHelper.getHealthy().get(position).energy + " kcal", "Calorie"));
        adapterInfo.updateData(list);

        binding.tvWater.setText(dbHelper.getHealthy().get(position).water + "\n ml");
        Double persen = 0.05 * Integer.parseInt(dbHelper.getHealthy().get(position).water);
        binding.crWater.setPercent(Float.parseFloat(persen + ""));
        binding.tvPersenWater.setText(persen + " %");

        binding.tvStep.setText(dbHelper.getHealthy().get(position).step + "\n bước");
        gender = dbHelper.getInformation().gender;
        weight = Float.parseFloat(dbHelper.getInformation().weight.replace("kg", "").trim());
        height = Float.parseFloat(dbHelper.getInformation().height.replace("cm", "").trim());
        RMR = cacularWithOcc(dbHelper.getInformation().occu, cacularRMR(gender, weight, height, old)) / 20;

        binding.crRun.setPercent((float) ((Integer.parseInt(dbHelper.getHealthy().get(position).step) / RMR)) * 100);
        binding.tvPersenStep.setText((float) Math.round(((Integer.parseInt(dbHelper.getHealthy().get(position).step) / RMR)) * 100) + "%");


        binding.crSleep.setPercent((float) (0.09 * 100 * Float.parseFloat(dbHelper.getHealthy().get(position).sleep)));
        binding.tvSleep.setText(dbHelper.getHealthy().get(position).sleep + " h");
        if (Float.parseFloat(dbHelper.getHealthy().get(position).sleep) > 9) {
            binding.tvStatusSleep.setText("Không tốt");
        } else if (Float.parseFloat(dbHelper.getHealthy().get(position).sleep) <= 9 && Float.parseFloat(dbHelper.getHealthy().get(position).sleep) >= 8) {
            binding.tvStatusSleep.setText("Rất tốt");
        } else if (Float.parseFloat(dbHelper.getHealthy().get(position).sleep) < 8) {
            binding.tvStatusSleep.setText("Chưa tốt");
        }
        if (Float.parseFloat(dbHelper.getHealthy().get(position).heart) > 80 || Float.parseFloat(dbHelper.getHealthy().get(position).heart) != 0 && Float.parseFloat(dbHelper.getHealthy().get(position).heart) < 60) {
            binding.DanhGia.setVisibility(View.VISIBLE);
            binding.DanhGia.setText("Bạn cần được nghỉ ngơi, nhịp tim của bạn không ổn định!!");
        } else {
            binding.DanhGia.setVisibility(View.GONE);
        }

    }


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

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        Log.d("Trang mess", messageEvent.getPath());

    }

    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(getActivity()).removeListener(this);
        Wearable.getMessageClient(getActivity()).removeListener(this);
        Wearable.getCapabilityClient(getActivity()).removeListener(this);
    }


}
