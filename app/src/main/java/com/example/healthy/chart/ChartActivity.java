package com.example.healthy.chart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.databinding.ActivityChartBinding;
import com.example.healthy.model.Healthy;
import com.example.healthy.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import im.dacer.androidcharts.LineView;

public class ChartActivity extends AppCompatActivity {
    ActivityChartBinding binding;
    int randomint = 0;
    DbHelper dbHelper;
    List<Healthy> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chart);
        dbHelper = new DbHelper(this);
        list = new ArrayList<>();
        list = dbHelper.getHealthy();
        Calendar cal = Calendar.getInstance();
        int res = cal.getActualMaximum(Calendar.DATE);
        System.out.println("Today's Date = " + cal.getTime());
        System.out.println("Last Date of the current month = " + res);

        randomint = res;

        initLineView(binding.lineViewFloat);


        randomSet(binding.lineViewFloat);
    }

    int j = 0;

    private void initLineView(LineView lineView) {
        j = Integer.parseInt(list.get(0).date.substring(8, 10)) - 1;
        ArrayList<String> test = new ArrayList<String>();

        for (int i = j; i < randomint; i++) {
            test.add(String.valueOf(i + 1));
        }
        lineView.setBottomTextList(test);
        lineView.setColorArray(new int[]{
                Color.parseColor("#36F475"), Color.parseColor("#9C27B0"),
                Color.parseColor("#2196F3"), Color.parseColor("#009688"),

        });
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
    }

    private void randomSet(LineView lineViewFloat) {

        ArrayList<Float> dataListF = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dataListF.add(Float.parseFloat(list.get(i).Calorie));
        }

        ArrayList<Float> dataListF2 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dataListF2.add(Float.parseFloat(list.get(i).energy));
        }

        ArrayList<Float> dataListF3 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dataListF3.add(Float.parseFloat(list.get(i).water));
        }

        ArrayList<Float> dataListF4 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            dataListF4.add(Float.parseFloat(list.get(i).sleep));
        }

        ArrayList<ArrayList<Float>> dataListFs = new ArrayList<>();
        dataListFs.add(dataListF);
        dataListFs.add(dataListF2);
        dataListFs.add(dataListF3);
        dataListFs.add(dataListF4);

        lineViewFloat.setFloatDataList(dataListFs);
    }
}