package com.example.fitty.Adapters;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.example.fitty.services.TestService;

public class Util {

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TestService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1*1000);
        builder.setOverrideDeadline(3*1000);

        JobScheduler scheduler = context.getSystemService(JobScheduler.class);
        scheduler.schedule(builder.build());
    }

}
