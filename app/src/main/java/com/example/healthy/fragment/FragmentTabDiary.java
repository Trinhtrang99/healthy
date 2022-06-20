package com.example.healthy.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.healthy.R;
import com.example.healthy.databinding.FragmentTabViewpagerBinding;


public class FragmentTabDiary extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private String title;
    private int page;
    private FragmentTabViewpagerBinding binding;
    private boolean isFirst;


    public static FragmentTabDiary newInstance() {
        FragmentTabDiary fragmentFirst = new FragmentTabDiary();
        Bundle args = new Bundle();
      //  args.putInt("someInt", page);
        //   args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_viewpager, container, false);

        binding.check1.setOnCheckedChangeListener(this);
        binding.check2.setOnCheckedChangeListener(this);
        binding.check3.setOnCheckedChangeListener(this);
        binding.check4.setOnCheckedChangeListener(this);
        binding.check5.setOnCheckedChangeListener(this);
        binding.check6.setOnCheckedChangeListener(this);
        return binding.getRoot();
    }




    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.check1:
                checkVisibility(b, binding.txtReach1);
                break;
            case R.id.check2:
                checkVisibility(b, binding.txtReach2);
                break;
            case R.id.check3:
                checkVisibility(b, binding.txtReach3);
                break;
            case R.id.check4:
                checkVisibility(b, binding.txtReach4);
                break;
            case R.id.check5:
                checkVisibility(b, binding.txtReach5);
                break;
            case R.id.check6:
                checkVisibility(b, binding.txtReach6);
                break;
        }
    }

    private void checkVisibility(boolean b, TextView tv) {
        if (b) {
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.INVISIBLE);
        }
    }


}
