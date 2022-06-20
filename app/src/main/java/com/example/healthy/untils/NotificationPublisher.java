package com.example.healthy.untils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.healthy.R;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                .setSmallIcon(R.drawable.ic_drink)
                .setContentTitle("Tới giờ uống nước")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(200, builder.build());

    }
}