package com.example.healthy;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.health.services.client.HealthServices;
import androidx.health.services.client.HealthServicesClient;
import androidx.health.services.client.PassiveMonitoringClient;
import androidx.health.services.client.data.DataType;
import androidx.health.services.client.data.PassiveMonitoringCapabilities;
import androidx.wear.ambient.AmbientModeSupport;

import com.example.healthy.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SensorEventListener, AmbientModeSupport.AmbientCallbackProvider,
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener {

    private RelativeLayout btnHeart;
    private ActivityMainBinding binding;

    private static final String TAG = "MY_TASK";

    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor mHeartRateSensor;
    private SensorManager mSensorManager;
    private CountDownLatch latch;
    public String countStep = "0";
    public String heartRate = "0";
    AmbientModeSupport.AmbientController ambientController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ambientController = AmbientModeSupport.attach(MainActivity.this);
        btnHeart = binding.btnHeart;
        btnHeart.setOnClickListener(view -> {
            Intent i = new Intent(this, Detail.class);
            i.putExtra("type", 1);
            i.putExtra("HEART",heartRate);
            startActivity(i);
        });
        binding.btnBlood.setOnClickListener(view -> {
            Intent i = new Intent(this, Detail.class);
            i.putExtra("type", 2);

            startActivity(i);
        });
        binding.rootStep.setOnClickListener(v -> {
            Intent i = new Intent(this, StepCount.class);
            i.putExtra("type", 3);
            i.putExtra("COUNTSTEP", countStep);
            startActivity(i);
        });

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (keep) {

            }
        }

        latch = new CountDownLatch(1);
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        if (mHeartRateSensor == null)
            Log.d(TAG, "heart rate sensor is null");


        HealthServicesClient healthClient = HealthServices.getClient(this /*context*/);
        PassiveMonitoringClient passiveClient = healthClient.getPassiveMonitoringClient();

        ListenableFuture<PassiveMonitoringCapabilities> capabilitiesFuture = passiveClient.getCapabilities();
        Futures.addCallback(capabilitiesFuture,
                new FutureCallback<PassiveMonitoringCapabilities>() {
                    @Override
                    public void onSuccess(@Nullable PassiveMonitoringCapabilities result) {
                        Boolean supportsHeartRate = result
                                .getSupportedDataTypesPassiveMonitoring()
                                .contains(DataType.HEART_RATE_BPM);
                        Boolean supportsStepsEvent = result
                                .getSupportedDataTypesEvents()
                                .contains(DataType.FLAT_GROUND_DURATION);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // display an error
                    }
                },
                ContextCompat.getMainExecutor(this /*context*/));

        int running_step = DataType.RUNNING_STEPS.getFormat();
        int heart_rate_bpm = DataType.HEART_RATE_BPM.getFormat();
        int spo2Format = DataType.SPO2.getFormat();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            latch.await();
            if (sensorEvent.values[0] > 0) {

                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);

            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "accuracy changed: " + i);
        // accuracy.setText("Accuracy: " + Integer.toString(i));
    }

    //Task that shows the path of a received message


    @Override
    protected void onStop() {
        super.onStop();

        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey("keep")) {
            boolean keep = intent.getExtras().getBoolean("keep");
            if (!keep) {
                finish();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mSensorManager.registerListener(this, this.mHeartRateSensor, 3);
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {

        return new MyAmbientCallback();
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/HEAR_RATE")) {
            heartRate = new String(messageEvent.getData(), StandardCharsets.UTF_8);

        }else {
            countStep = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        }
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
        }

        @Override
        public void onExitAmbient() {
            super.onExitAmbient();
        }

        @Override
        public void onUpdateAmbient() {
            super.onUpdateAmbient();
        }

        public MyAmbientCallback() {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Wearable.getDataClient(this).addListener(this);
            Wearable.getMessageClient(this).addListener(this);
            Wearable.getCapabilityClient(this)
                    .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        try {
            // Block on a task and get the result synchronously (because this is on a background
            // thread).
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes) {
                results.add(node.getId());
            }
        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);
        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
        return results;
    }

    @WorkerThread
    private void sendStartActivityMessage(String node, String event) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, "/APP_OPEN_WEARABLE_PAYLOAD", new byte[0]);

        try {

            Integer result = Tasks.await(sendMessageTask);


        } catch (ExecutionException exception) {
            Log.e("TAG", "Task failed: " + exception);

        } catch (InterruptedException exception) {
            Log.e("TAG", "Interrupt occurred: " + exception);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
        Wearable.getMessageClient(this).removeListener(this);
        Wearable.getCapabilityClient(this).removeListener(this);
    }
}