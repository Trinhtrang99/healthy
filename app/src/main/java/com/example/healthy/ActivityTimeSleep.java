package com.example.healthy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.a1573595.clockslider.ClockSlider;
import com.example.healthy.alarm.AlarmReceiver;
import com.example.healthy.databinding.ActivityTimeSleepBinding;
import com.example.healthy.service.StepService;
import com.example.healthy.sqlite.DbHelper;
import com.example.healthy.untils.Constants;

import java.util.Calendar;

public class ActivityTimeSleep extends AppCompatActivity {
    ActivityTimeSleepBinding binding;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private int hour;
    private int munie;
    public static double timeS = 0;
    Double e;
    private StepService service = null;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_sleep);
        dbHelper = new DbHelper(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        binding.clockSlider.setOnTimeChangedListener(new ClockSlider.OnTimeChangedListener() {
            @Override
            public void onStartChanged(int i, int i1) {
                binding.timeSleep.setText(i + ":" + i1 + " pm");
                hour = i;
                munie = i1;
            }

            @Override
            public void onEndChanged(int i, int i1) {
                binding.timeWakeup.setText(i + ":" + i1 + " am");
            }
        });
        binding.imgBack.setOnClickListener(view -> {
            finish();
        });
        binding.tvSave.setOnClickListener(view -> {
            float c = 0;
            float a = binding.clockSlider.getEndHours();
            float b = binding.clockSlider.getStartHours();
            if (a > b) {
                c = a - b;
            } else {
                c = b - a;
            }
            float d = 24 - c;
            e = (double) Math.round(d * 10) / 10;
            if (e < 7 || e > 10) {
                showDialog(this, "Bạn hãy đảm bảo ngủ từ 7-10 tiếng một ngày \n Bạn có chắc chắn thiết lập thời gian trên ?");
            } else {
                showDialog(this, "Bạn đã thiết lập thời gian ngủ thành công!");
            }

        });

    }

    public void showDialog(Context context, String txt) {
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_err, null);

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        dialogBuilder.setView(view);
        android.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_login);
        alertDialog.show();
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        alertDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView txtCancel = alertDialog.findViewById(R.id.txt_cancel);
        TextView txtRetry = alertDialog.findViewById(R.id.txt_retry);
        TextView txtMess = alertDialog.findViewById(R.id.txt_message);
        txtMess.setText(txt);
        txtCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        txtRetry.setOnClickListener(v -> {
            dbHelper.updateHealthy(Constants.SLEEP, e.toString(), dbHelper.getHealthy().get(dbHelper.getHealthy().size() - 1).id);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 16);
            calendar.set(Calendar.MINUTE, 26);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Intent intent = new Intent(this, AlarmReceiver.class);
            Bundle bundle = new Bundle();
            intent.putExtra(context.getString(R.string.bundle_alarm_obj), bundle);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
            service.startForegroundServiceWithAlarm("Tới giờ đi ngủ rồi");
            alertDialog.dismiss();
            finish();
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder_service) {
            StepService.MyBinder myBinder = (StepService.MyBinder) binder_service;
            service = myBinder.getService();

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, StepService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }
}