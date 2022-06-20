package com.example.healthy.calories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.databinding.ActivityCaloriesBinding;
import com.example.healthy.model.Protein;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CaloriesActivity extends AppCompatActivity {
    private ActivityCaloriesBinding binding;
//    Tính chỉ số BMR đối với nữ giới: BMR = 655.1 + (9.563 x cân nặng) + (1.850 x chiều cao) – (4.676 x tuổi)
//    Tính chỉ số BMR đối với nam giới: BMR = 66.47 + (13.75 x cân nặng) + (5.003 x chiều cao) – (6.755 x tuổi)

    private DbHelper dbHelper;
    private Double BMR = 0.0;
    private String gender;
    private float weight, height;
    private int old;
    private int position;
    private double protein;
    private float lbs;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calories);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        dbHelper = new DbHelper(this);
        gender = dbHelper.getInformation().gender;
        weight = Float.parseFloat(dbHelper.getInformation().weight.replace("kg", "").trim());
        height = Float.parseFloat(dbHelper.getInformation().height.replace("cm", "").trim());
        position = dbHelper.getHealthy().size() - 1;
        protein = weight * 1.1;
        DateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = (Date) parser.parse(dbHelper.getInformation().birthday);
            old = 2022 - date.getYear();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BMR = cacularWithOcc(dbHelper.getInformation().occu, cacularBMR(gender, weight, height, old));
        binding.Rate.setText("Cơ thể bạn cần nạp vào lượng Kcal là " + Math.round((BMR * 100.0) / 100.0) + " Kcal");
        binding.tvCalo.setText(dbHelper.getHealthy().get(position).Calorie + " Kcal");

        binding.imgAddBreak.setOnClickListener(view -> {
            Intent i = new Intent(CaloriesActivity.this, MealDetailActivity.class);
            i.putExtra("Title", Constants.TYPE_MORNING);
            i.putExtra("Kcal", Math.round((BMR * 100.0) / 100.0) + "");
            startActivity(i);
        });
        binding.imgAddLunch.setOnClickListener(view -> {
            Intent i = new Intent(CaloriesActivity.this, MealDetailActivity.class);
            i.putExtra("Title", Constants.TYPE_LUNCH);
            i.putExtra("Kcal", Math.round((BMR * 100.0) / 100.0) + "");
            startActivity(i);
        });
        binding.iconAddDinner.setOnClickListener(view -> {
            Intent i = new Intent(CaloriesActivity.this, MealDetailActivity.class);
            i.putExtra("Title", Constants.TYPE_LUNCH);
            i.putExtra("Kcal", Math.round((BMR * 100.0) / 100.0) + "");
            startActivity(i);
        });


        if (dbHelper.getProtein().date == null ||
                !dbHelper.getProtein().date.equals(dtf.format(now))
        ) {
            dbHelper.addProtein(new Protein("0", "0", "0", dtf.format(now)));
        }
    }

    public Double cacularBMR(String gender, float kg, float cm, int old) {
        double BMR = 0;
        if (gender.equals(getString(R.string.nu))) {
            BMR = 655.1 + (9.563 * kg) + (1.850 * cm) - (4.676 * old);
        } else {
            BMR = 66.47 + (13.75 * kg) + (5.003 * cm) - (6.755 * old);
        }
        return BMR;
    }

    public Double cacularWithOcc(String occu, Double BMR) {

        if (occu.equals(getString(R.string.it_vd))) {
            return BMR * 1.2;
        } else if (occu.equals(getString(R.string.vd_nhe))) {
            return BMR * 1.375;

        } else if (occu.equals(getString(R.string.vd_vua_phai))) {
            return BMR * 1.55;

        } else if (occu.equals(getString(R.string.vd_nhieu))) {
            return BMR * 1.725;

        } else if (occu.equals(getString(R.string.vd_cao))) {
            return BMR * 1.9;

        }
        return BMR;

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        lbs = (float) (2.2 * weight);

        int i = (int) Double.parseDouble(dbHelper.getProtein().protein);
        binding.tvValueProtein.setText(i + "/" + lbs + "g");
        binding.process1.setMax((int) lbs);
        binding.process1.setProgress(i);

        int i2 = (int) (lbs * 0.3);
        int i22 = (int) Double.parseDouble(dbHelper.getProtein().fat);
        binding.tvValueFat.setText(i22 + "/" + i2 + "g");
        binding.process2.setMax(i2);
        binding.process2.setProgress(i22);

        binding.process3.setMax((int) Math.round((BMR * 100.0) / 100.0));
        binding.tvValueCar.setText(dbHelper.getProtein().kcal + "/" + Math.round((BMR * 100.0) / 100.0) + "kcal");
        int i3 = (int) Double.parseDouble(dbHelper.getProtein().kcal);
        binding.process3.setProgress(i3);

        float persen = (float) (100 / BMR);
        int i4 = (int) (Float.parseFloat(dbHelper.getProtein().kcal) * persen);
        binding.crRun.setPercent(i4);
        binding.tvCalo.setText(dbHelper.getProtein().kcal + "\nKcal");
    }
}