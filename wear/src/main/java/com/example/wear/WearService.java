package com.example.wear;

import android.content.Intent;

import android.util.Log;

import com.example.wear.lib.TeleportService;


public class WearService extends TeleportService {

    private static final String TAG = WearService.class.getName();

    // Teleport config
    public static final String START_ACTIVITY = "startActivity";
    public static final String STOP_ACTIVITY = "stopActivity";

    @Override
    public void onCreate() {
        super.onCreate();
        setOnGetMessageTask(new ActivityManagementTask());
    }

    //Task that shows the path of a received message
    public class ActivityManagementTask extends TeleportService.OnGetMessageTask {
        @Override
        protected void onPostExecute(String  path) {
            if (path.equals(START_ACTIVITY)){
                Intent startIntent = new Intent(getBaseContext(), MainActivity.class);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startIntent.putExtra("keep", true);
                startActivity(startIntent);
            } else if (path.equals(STOP_ACTIVITY)) {
                Intent stopIntent = new Intent(getBaseContext(), MainActivity.class);
                stopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                stopIntent.putExtra("keep", false);
                startActivity(stopIntent);
            } else {
                Log.d(TAG, "Got a message with path: " + path);
            }
            //let's reset the task (otherwise it will be executed only once)
            setOnGetMessageTask(new ActivityManagementTask());
        }
    }
}