package com.example.healthy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.healthy.R;
import com.example.healthy.databinding.FragmentProfileBinding;
import com.example.healthy.sqlite.DbHelper;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    DbHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.rlWeight.setOnClickListener(view -> {
            showChangeInfo();
        });
        dbHelper = new DbHelper(requireContext());
        binding.save.setOnClickListener(view -> {
            dbHelper.updateInformation(binding.txtTall.getText().toString()
                    , binding.txtWeight.getText().toString(), dbHelper.getInformation().id);

        });


        binding.txtName.setText(dbHelper.getInformation().name);
        binding.txtTall.setText(dbHelper.getInformation().height);
        binding.txtWeight.setText(dbHelper.getInformation().weight);
        binding.txtGender.setText(dbHelper.getInformation().gender);
        binding.txtBirth.setText(dbHelper.getInformation().birthday);
        binding.txtProfess.setText(dbHelper.getInformation().occu);
        return binding.getRoot();
    }

    private void showChangeInfo() {

        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.change_infor, null);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(requireContext());
        dialogBuilder.setView(view);
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_login);
        alertDialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText edtEmail = alertDialog.findViewById(R.id.edt_mail);
        RelativeLayout rlCancel = alertDialog.findViewById(R.id.rl_cancel);
        RelativeLayout rlChange = alertDialog.findViewById(R.id.rlChange);
        rlCancel.setOnClickListener(view1 -> alertDialog.dismiss());
        rlChange.setOnClickListener(view1 -> {
            binding.txtWeight.setText(edtEmail.getText().toString() + "kg");
            alertDialog.dismiss();
        });
    }
}
