package com.example.healthy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ClockPieView pieView = (ClockPieView) findViewById(R.id.clock_pie_view);
        ArrayList<ClockPieHelper> pieHelperArrayList = new ArrayList<ClockPieHelper>();
        pieView.setDate(pieHelperArrayList);
    }
}