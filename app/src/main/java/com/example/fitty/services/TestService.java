package com.example.fitty.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


import java.util.Calendar;

public class TestService extends Service {

    public TestService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE", "................................................................service started");
        boolean i = true;
        long startTime = Calendar.getInstance().getTimeInMillis();
        while (i) {
//            try {
//                Thread.sleep(1000);
//
//                Intent inten = new Intent("just_an_intent");
//                inten.putExtra("value", i);
//                sendBroadcast(inten);
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            long time = ( Calendar.getInstance().getTimeInMillis() - startTime ) / 1000;
            Log.i("SERVICE", "............................................................" + time);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SERVICE", "................................................................service destroyed");
    }

}
