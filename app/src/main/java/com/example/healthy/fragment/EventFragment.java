package com.example.healthy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.healthy.R;
import com.example.healthy.adapter.AdapterEvent;
import com.example.healthy.databinding.FragmentEventBinding;
import com.example.healthy.untils.DataLocal;

public class EventFragment extends Fragment {
    FragmentEventBinding binding;
    AdapterEvent adapterEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false);
        adapterEvent = new AdapterEvent(DataLocal.getInstance().getListEvent(), requireContext());
        binding.rc.setAdapter(adapterEvent);
        return binding.getRoot();
    }
}
