package com.example.healthy;

import android.os.Bundle;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.healthy.databinding.ActivityPlayVideoBinding;
import com.example.healthy.untils.DataLocal;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

public class ActivityPlayVideo extends YouTubeBaseActivity {
    ActivityPlayVideoBinding binding;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_video);
        if (getIntent() != null) {
            videoId = getIntent().getStringExtra("video");
        }
        binding.playvideo.initialize(DataLocal.getInstance().key, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("Cj7Y8_Xfi3k");
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(ActivityPlayVideo.this, "faile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}