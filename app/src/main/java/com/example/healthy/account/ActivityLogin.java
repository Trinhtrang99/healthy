package com.example.healthy.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.healthy.LIB.TeleportClient;
import com.example.healthy.R;
import com.example.healthy.base.BaseActivity;
import com.example.healthy.databinding.ActivityLoginBinding;
import com.example.healthy.heart_rate.HeartRate;
import com.google.android.gms.wearable.DataMap;

public class ActivityLogin extends BaseActivity {
    private ActivityLoginBinding binding;
    TeleportClient mTeleportClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
       // getAqi();


        binding.rootRegis.setOnClickListener(view -> {
            startActivity(new Intent(this, ActivityRegister.class));
            finish();
        });

        mTeleportClient = new TeleportClient(this);
        binding.btnLogin.setOnClickListener(view -> {

            startActivity(new Intent(this, HeartRate.class));
            finish();
            //   syncDataItem();
            //sendStartActivityMessage();
            //sendMessage();

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTeleportClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTeleportClient.disconnect();

    }

    public void syncDataItem() {
        mTeleportClient.syncString("hello", "ABC");

    }

    public void sendMessage() {

        mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());

        mTeleportClient.sendMessage("hello", null);
        Log.d("MYTASK", "ABC");
    }

    public void sendStartActivityMessage() {

        mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());

        mTeleportClient.sendMessage("hello", null);
        Log.d("MYTASK", "startActivity");
    }


    public class ShowToastOnSyncDataItemTask extends TeleportClient.OnSyncDataItemTask {

        @Override
        protected void onPostExecute(DataMap dataMap) {

            String s = dataMap.getString("hello");


            Toast.makeText(getApplicationContext(), "DataItem - " + s, Toast.LENGTH_SHORT).show();

            //let's reset the task (otherwise it will be executed only once)
            mTeleportClient.setOnSyncDataItemTask(new ShowToastOnSyncDataItemTask());
        }
    }


    public class ShowToastFromOnGetMessageTask extends TeleportClient.OnGetMessageTask {

        @Override
        protected void onPostExecute(String path) {


            Toast.makeText(getApplicationContext(), "Message - " + path, Toast.LENGTH_SHORT).show();

            //let's reset the task (otherwise it will be executed only once)
            mTeleportClient.setOnGetMessageTask(new ShowToastFromOnGetMessageTask());
        }
    }


}