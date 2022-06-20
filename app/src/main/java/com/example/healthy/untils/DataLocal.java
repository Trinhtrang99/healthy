package com.example.healthy.untils;

import com.example.healthy.R;
import com.example.healthy.model.Event;
import com.example.healthy.model.Information;

import java.util.ArrayList;
import java.util.List;

public class DataLocal {
    private static DataLocal instance;

    public static DataLocal getInstance() {
        if (instance == null) {
            instance = new DataLocal();
        }
        return instance;
    }

    public String key = "AIzaSyCR9owVOuMyRJt71VtOOgWIjUI-PS18cyU";

    public List<Event> getListEvent() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(new Event("Các bài tập Yoga", R.drawable.yoga,"PLknC8iDtTlBYb7_m8W6qQUOauXGpHNm4W"));
        eventList.add(new Event("Các bài tập Workout", R.drawable.workout,"PLlw1lk_UliYuUdbIVZIqe4HPmXxhVePQh"));
        eventList.add(new Event("Các bài tập Earobic", R.drawable.earobic,"PLAzcNn86yUgdlm2SXfsUwpmnMo08yz2xy"));
        return eventList;
    }
    public List<Information> getListInfor(){
        List<Information> informationList = new ArrayList<>();
        informationList.add(new Information(R.color.red,R.drawable.heart,"70 nhịp/phút","Nhịp tim"));
        informationList.add(new Information(R.color.txt_color,R.drawable.huyetap,"120/80 mmHg","Huyết áp"));
        informationList.add(new Information(R.color.main_color,R.drawable.ic_baseline_height_24,"150 cm","Chiều cao"));
        informationList.add(new Information(R.color.yellow,R.drawable.weight,"45 kg","Cân nặng"));
        informationList.add(new Information(R.color.green_018577,R.drawable.bmi,"22 (bình thường)","BMI"));
        informationList.add(new Information(R.color.blue_2c3764,R.drawable.clipart1775369,"22 (bình thường)","Calorie"));
        return informationList;
    }
}
