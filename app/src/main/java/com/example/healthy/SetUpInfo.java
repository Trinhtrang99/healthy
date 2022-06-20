package com.example.healthy;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.databinding.ActivitySetUpInfoBinding;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.overlay.BalloonOverlayCircle;

import java.text.DecimalFormat;

public class SetUpInfo extends AppCompatActivity {
    ActivitySetUpInfoBinding binding;
    Balloon balloon;
    Double height, weight, Bmi;
    String mess;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_up_info);

        height = getIntent().getDoubleExtra("Height", 0) / 100;
        weight = getIntent().getDoubleExtra("Weight", 0);
        Bmi = weight / (height * height);

        String patternPhanTram = "###.##";
        DecimalFormat formatPhanTram = new DecimalFormat(patternPhanTram);
        String stringPhanTram = formatPhanTram.format(Bmi);

        if (Bmi < 18.5) {
            mess = "Chỉ số BMI của bạn : " + stringPhanTram + "\n Bạn đang thiếu cân";
        } else if (18.5 <= Bmi && 22.9 >= Bmi) {
            mess = "Chỉ số BMI của bạn : " + stringPhanTram + "\n Bạn đang có cân nặng lý tưởng";
        } else {
            mess = "Chỉ số BMI của bạn : " + stringPhanTram + "\n Bạn đang thừa cân";
        }

        balloon = new Balloon.Builder(getApplicationContext())
                .setLayout(R.layout.layoutmes)
                .setArrowColor(getColor(R.color.white))
                .setArrowPosition(0.84f)
                .setArrowSize(15)
                .setMarginLeft(80)
                .setIsVisibleOverlay(false)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayShape(new BalloonOverlayCircle(0f))
                .setCornerRadius(12f)
                .build();
        TextView tvMess = balloon.getContentView().findViewById(R.id.txt_message);
        tvMess.setText(mess);

        balloon.showAlignBottom(binding.txtTt);
        binding.txtTt.setOnClickListener(view -> {
            balloon.showAlignBottom(binding.txtTt);

        });
    }
}