package com.example.healthy.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.healthy.R;
import com.example.healthy.adapter.PageAdapter;
import com.example.healthy.chart.ChartActivity;
import com.example.healthy.databinding.FragmentDiaryBinding;
import com.example.healthy.untils.DateUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DiaryFragment extends Fragment {
    FragmentDiaryBinding binding;
    private PageAdapter viewPagerDateAdapter;
    private List<String> dates;
    private DatePickerDialog datePickerDialog;
    private String formatted;
    private String formattedMonth;
    private String formattedDay;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDayOfMonth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false);
        dates = new ArrayList<>();
        binding.imgCalender.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ChartActivity.class);
            startActivity(i);
        });

        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd");

        formatted = current.format(formatterYear);
        formattedMonth = current.format(formatterMonth);
        formattedDay = current.format(formatterDay);

        selectedYear = Integer.parseInt(formatted);
        selectedMonth = Integer.parseInt(formattedMonth) - 1;
        selectedDayOfMonth = Integer.parseInt(formattedDay);

        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(formatted), Integer.parseInt(formattedMonth));
        int daysInMonth = yearMonthObject.lengthOfMonth();
        dates = DateUtils.getInstance().listDayOfMoth(daysInMonth, Integer.parseInt(formattedMonth), Integer.parseInt(formatted));

        viewPagerDateAdapter = new PageAdapter(getChildFragmentManager());
        for (int i = 0; i < 24; i++) {
            viewPagerDateAdapter.addFragment(FragmentTabDiary.newInstance(), dates.get(i));
        }
        binding.viewPager.setAdapter(viewPagerDateAdapter);
        binding.tablayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setCurrentItem(Integer.parseInt(formattedDay) - 1);

        return binding.getRoot();
    }


}
