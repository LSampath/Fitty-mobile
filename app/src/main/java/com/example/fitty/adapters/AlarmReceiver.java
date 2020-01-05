package com.example.fitty.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.fitty.models.AppData;
import com.example.fitty.services.SleepDetectorService;
import com.example.fitty.services.StepCountService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SERVICE", ".............................." + intent.getExtras().getInt(AppData.RECEIVER_CODE));

        int receiver_code = intent.getExtras().getInt(AppData.RECEIVER_CODE);

        if (receiver_code == AppData.COUNTER_START_RECEIVER) {
            Intent i = new Intent(context, StepCountService.class);
            context.startService(i);

        } else if (receiver_code == AppData.COUNTER_STOP_RECEIVER) {
            Intent i = new Intent(context, StepCountService.class);
            context.stopService(i);

        } else if (receiver_code == AppData.SLEEP_START_RECEIVER) {
            Intent i = new Intent(context, SleepDetectorService.class);
            context.startService(i);

        } else if (receiver_code == AppData.SLEEP_STOP_RECEIVER) {
            Intent i = new Intent(context, SleepDetectorService.class);
            context.stopService(i);
        }

    }
}
