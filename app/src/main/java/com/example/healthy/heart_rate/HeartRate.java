package com.example.healthy.heart_rate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.healthy.R;
import com.example.healthy.databinding.ActivityHeartRateBinding;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;
import com.google.android.material.snackbar.Snackbar;

public class HeartRate extends AppCompatActivity {
    private OutputAnalyzer analyzer;

    private final int REQUEST_CODE_CAMERA = 0;
    public static final int MESSAGE_UPDATE_REALTIME = 1;
    public static final int MESSAGE_UPDATE_FINAL = 2;
    public static final int MESSAGE_CAMERA_NOT_AVAILABLE = 3;

    private boolean justShared = false;
    ActivityHeartRateBinding binding;
    DbHelper dbHelper;

    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_UPDATE_REALTIME) {
                binding.textView.setText(msg.obj.toString() + " BPM");
                binding.crRun.setPercent(Float.parseFloat(msg.obj.toString()));
            }

            if (msg.what == MESSAGE_UPDATE_FINAL) {
//                ((EditText) findViewById(R.id.editText)).setText(msg.obj.toString());
//
//                // make sure menu items are enabled when it opens.
//                Menu appMenu = ((Toolbar) findViewById(R.id.toolbar)).getMenu();
//
//                setViewState(VIEW_STATE.SHOW_RESULTS);
            }

            if (msg.what == MESSAGE_CAMERA_NOT_AVAILABLE) {
                Log.println(Log.WARN, "camera", msg.obj.toString());

                ((TextView) findViewById(R.id.textView)).setText(
                        R.string.camera_not_found
                );
                analyzer.stop();
            }
        }
    };

    private final CameraService cameraService = new CameraService(this, mainHandler);


    @Override
    protected void onResume() {
        super.onResume();

        analyzer = new OutputAnalyzer(this, binding.graphTextureView, mainHandler);

        // TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = binding.textureView2.getSurfaceTexture();

        // justShared is set if one clicks the share button.
        if ((previewSurfaceTexture != null) && !justShared) {
            Surface previewSurface = new Surface(previewSurfaceTexture);

            // show warning when there is no flash
            if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.noFlashWarning),
                        Snackbar.LENGTH_LONG
                ).show();
            }

            cameraService.start(previewSurface);
            analyzer.measurePulse(binding.textureView2, cameraService);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraService.stop();
        if (analyzer != null) analyzer.stop();
        analyzer = new OutputAnalyzer(this, binding.graphTextureView, mainHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_heart_rate);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE_CAMERA);
        dbHelper = new DbHelper(this);
        dbHelper.createDataBase();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Snackbar.make(
                        findViewById(R.id.constraintLayout),
                        getString(R.string.cameraPermissionRequired),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }

    public void onClickNewMeasurement() {
        analyzer = new OutputAnalyzer(this, findViewById(R.id.graphTextureView), mainHandler);

        // clear prior results
        char[] empty = new char[0];
//        ((EditText) findViewById(R.id.editText)).setText(empty, 0, 0);
//        ((TextView) findViewById(R.id.textView)).setText(empty, 0, 0);
//
//        // hide the new measurement item while another one is in progress in order to wait
//        // for the previous one to finish
//        // Exporting results cannot be done, either, as it would read from the already cleared UI.
//        setViewState(VIEW_STATE.MEASUREMENT);

        TextureView cameraTextureView = findViewById(R.id.textureView2);
        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();

        if (previewSurfaceTexture != null) {
            // this first appears when we close the application and switch back
            // - TextureView isn't quite ready at the first onResume.
            Surface previewSurface = new Surface(previewSurfaceTexture);
            cameraService.start(previewSurface);
            analyzer.measurePulse(cameraTextureView, cameraService);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbHelper.updateHealthy(Constants.HEART,binding.textView.getText().toString().replace("BPM", ""), dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).id);
    }
}