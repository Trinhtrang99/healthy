package com.example.healthy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.databinding.DataBindingUtil;

import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityInfoHealthyBinding;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.sqlite.Infomation;
import com.example.healthy.untils.SharedUtils;

import java.util.ArrayList;

public class ActivityInfoHealthy extends BaseActivity {
    ActivityInfoHealthyBinding binding;
    DbHelper dbHelper;
    String nameSpinner = "";
    ArrayList<String> gender = new ArrayList<>();
    ArrayList<String> occu = new ArrayList<>();
    String nameGender, nameOccu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info_healthy);
        dbHelper = new DbHelper(this);
        dbHelper.createDataBase();
        dbHelper.openDataBase();
        gender.add(getString(R.string.nam));
        gender.add(getString(R.string.nu));

        occu.add(getString(R.string.it_vd));
        occu.add(getString(R.string.vd_nhe));
        occu.add(getString(R.string.vd_vua_phai));
        occu.add(getString(R.string.vd_nhieu));
        occu.add(getString(R.string.vd_cao));
        setAdapterForSpinner(binding.spinGender, gender);
        setAdapterForSpinner(binding.spinWork, occu);

        binding.tvSave.setOnClickListener(view -> {
            if (binding.edtName.getText().toString().isEmpty()) {
                binding.tvNameError.setVisibility(View.VISIBLE);
            } else {
                binding.tvNameError.setVisibility(View.INVISIBLE);
            }
            if (binding.edtBirthDay.getText().toString().isEmpty()) {
                binding.tvBirthError.setVisibility(View.VISIBLE);
            } else {
                binding.tvBirthError.setVisibility(View.INVISIBLE);
            }
            if (binding.edtHeigh.getText().toString().isEmpty()) {
                binding.tvHeightError.setVisibility(View.VISIBLE);
            } else {
                binding.tvHeightError.setVisibility(View.INVISIBLE);
            }
            if (binding.edtWeigh.getText().toString().isEmpty()) {
                binding.tvWeightError.setVisibility(View.VISIBLE);
            } else {
                binding.tvWeightError.setVisibility(View.INVISIBLE);
            }
            if (binding.tvWeightError.getVisibility() == View.INVISIBLE
                    && binding.tvNameError.getVisibility() == View.INVISIBLE
                    && binding.tvHeightError.getVisibility() == View.INVISIBLE
                    && binding.tvBirthError.getVisibility() == View.INVISIBLE) {

                dbHelper.addInformation(new Infomation(binding.edtName.getText().toString(),
                        binding.edtBirthDay.getText().toString(),
                        binding.spinGender.getSelectedItem().toString(),
                        binding.edtHeigh.getText().toString(),
                        binding.edtWeigh.getText().toString(),
                        binding.spinWork.getSelectedItem().toString()));

                String waterNeed = String.valueOf(Float.parseFloat(binding.edtWeigh.getText().toString().replace("kg", "")) * 2 * 0.5 * 0.03 * 1000);
                SharedUtils.saveString(this, "WATER_NEED", waterNeed);
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        });


    }

    private void setAdapterForSpinner(Spinner spinner, ArrayList<String> arr) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nameSpinner = (String) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                nameSpinner = "";
            }
        });

    }
}