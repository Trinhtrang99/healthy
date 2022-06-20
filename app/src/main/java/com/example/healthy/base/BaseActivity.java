package com.example.healthy.base;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.healthy.R;

public class BaseActivity extends AppCompatActivity {
    private final FragmentBaseDialog customProgressDialogFragment = new FragmentBaseDialog();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setStatusBarWhite();
        super.onCreate(savedInstanceState);

    }

    public void setStatusBarWhite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    public void showProgressDialog(boolean show) {
        try {
            if (show) {
                if (!customProgressDialogFragment.isShowing()) {
                    customProgressDialogFragment.setShowing(true);
                    if (!customProgressDialogFragment.isAdded()) {
                        customProgressDialogFragment.show(getSupportFragmentManager(), customProgressDialogFragment.getTag());
                    }
                }
            } else {
                if (customProgressDialogFragment.isShowing()) {
                    customProgressDialogFragment.dismiss();
                    customProgressDialogFragment.setShowing(false);
                }
            }
        } catch (Exception ex) {

        }
    }
}
