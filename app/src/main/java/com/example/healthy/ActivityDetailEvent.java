package com.example.healthy;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.adapter.AdapterVideo;
import com.example.healthy.databinding.ActivityDetailEventBinding;
import com.example.healthy.network.ApiUtils;
import com.example.healthy.network.response.Video;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityDetailEvent extends AppCompatActivity {
    ActivityDetailEventBinding binding;
    AdapterVideo adapterVideo;
    private int img;
    private String playID;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event);
        if (getIntent() != null) {
            img = getIntent().getIntExtra("img", -1);
            playID = getIntent().getStringExtra("playId");
        }
        binding.imgTt.setImageDrawable(getDrawable(img));
        getVideo();
    }

    private void getVideo() {
        ApiUtils.getApiService().getVideo(playID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Video>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Video video) {

                        adapterVideo = new AdapterVideo(video.getList(), ActivityDetailEvent.this);
                        binding.rcExe.setAdapter(adapterVideo);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("kmfg", e.getMessage());
                    }
                });
    }
}