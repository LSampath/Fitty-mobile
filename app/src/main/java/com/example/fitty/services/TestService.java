package com.example.fitty.services;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.fitty.Adapters.Util;

public class TestService extends JobService {

    private static final String TAG = "SyncService";

    public TestService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        Intent service = new Intent(getApplicationContext(), LocalWordService.class);
        Log.i("TESTSERVICE", System.currentTimeMillis()+"");
        Util.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
