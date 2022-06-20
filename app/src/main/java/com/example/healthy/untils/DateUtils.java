package com.example.healthy.untils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private static DateUtils instance;

    public static DateUtils getInstance() {
        if (instance == null) {
            instance = new DateUtils();
        }
        return instance;
    }


    public String getDayByDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern("EEE");
        return sdf.format(date);
    }


    public List<String> listDayOfMoth(int limitDay, int month, int year) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < limitDay; i++) {
            list.add((i + 1) + "-" + "(" + getDayByDate(year + "-" + month + "-" + i) + ")");
        }
        return list;
    }
}
