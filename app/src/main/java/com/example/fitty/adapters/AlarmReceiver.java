package com.example.fitty.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.fitty.models.AppData;
import com.example.fitty.services.TestService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SERVICE", ".............................." + intent.getExtras().getInt(AppData.RECEIVER_CODE));

        int receiver_code = intent.getExtras().getInt(AppData.RECEIVER_CODE);

        if (receiver_code == AppData.COUNTER_START_RECEIVER) {

            Intent i = new Intent(context, TestService.class);
            context.startService(i);

        } else if (receiver_code == AppData.COUNTER_STOP_RECEIVER) {

            Intent i = new Intent(context, TestService.class);
            context.stopService(i);
        }

    }
}
