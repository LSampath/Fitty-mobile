package com.example.fitty.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;

import com.example.fitty.adapters.AlarmReceiver;
import com.example.fitty.R;
import com.example.fitty.adapters.DatabaseHelper;
import com.example.fitty.controllers.RunController;
import com.example.fitty.controllers.SleepController;
import com.example.fitty.controllers.StepController;
import com.example.fitty.models.AppData;
import com.example.fitty.models.RunningSession;
import com.example.fitty.services.SleepDetectorService;
import com.example.fitty.services.StepCountService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences(AppData.SHARED_PREF, MODE_PRIVATE);
        if (preferences.getBoolean(AppData.FIRST_TIME, true)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            // request permissions
            if (!checkPermissions(this, AppData.PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, AppData.PERMISSIONS, 1);
            }

            DatabaseHelper db = new DatabaseHelper(this);
            SleepController.insertHoursCustom(db, 10.5, -4);
            SleepController.insertHoursCustom(db, 8, -3);
            SleepController.insertHoursCustom(db, 7.36, -2);
            SleepController.insertHoursCustom(db, 9.5, -1);

            StepController.insertCountCustom(db, 13500, -5);
            StepController.insertCountCustom(db, 8020, -4);
            StepController.insertCountCustom(db, 12500, -3);
            StepController.insertCountCustom(db, 9540, -2);
            StepController.insertCountCustom(db, 1034, -1);

            RunController.insertSessionCustom(db, 10.14, 90, -3);
            RunController.insertSessionCustom(db, 13.14, 120, -2);
            RunController.insertSessionCustom(db, 8.54, 75, -1);

            //register broadcast receivers ////////////////////////////////////////////////////////////
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Step counter service
//            Intent counterStartAlarm = new Intent(this, AlarmReceiver.class);
//            counterStartAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_START_RECEIVER);
//            PendingIntent counterStartIntent = PendingIntent.getBroadcast(this, 243, counterStartAlarm, PendingIntent.FLAG_ONE_SHOT);
//            setRepeatingAlarm(counterStartIntent, alarmManager, 23, 36, 0);
//
//            Intent counterStopAlarm = new Intent(this, AlarmReceiver.class);
//            counterStopAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_STOP_RECEIVER);
//            PendingIntent counterStopIntent = PendingIntent.getBroadcast(this, 244, counterStopAlarm, PendingIntent.FLAG_ONE_SHOT);
//            setRepeatingAlarm(counterStopIntent, alarmManager, 23, 39, 0);
//
//            // Sleep detector service
//            Intent sleepStartAlarm = new Intent(this, AlarmManager.class);
//            sleepStartAlarm.putExtra(AppData.RECEIVER_CODE, AppData.SLEEP_START_RECEIVER);
//            PendingIntent sleepStartIntent = PendingIntent.getBroadcast(this, 245, sleepStartAlarm, PendingIntent.FLAG_ONE_SHOT);
//            setRepeatingAlarm(sleepStartIntent, alarmManager, 23, 40, 0);
//
//            Intent sleepStopAlarm = new Intent(this, AlarmManager.class);
//            sleepStopAlarm.putExtra(AppData.RECEIVER_CODE, AppData.SLEEP_STOP_RECEIVER);
//            PendingIntent sleepStopIntent = PendingIntent.getBroadcast(this, 246, sleepStopAlarm, PendingIntent.FLAG_ONE_SHOT);
//            setRepeatingAlarm(sleepStopIntent, alarmManager, 23, 42, 0);

            Intent intentStep = new Intent(this, StepCountService.class);
            this.startService(intentStep);

            Intent intentSleep = new Intent(this, SleepDetectorService.class);
            this.startService(intentSleep);

            // //////////////////////////////////////////////////////////////////////////////////////////
        }


        BottomNavigationView bottomNav = findViewById(R.id.navbar_bottom);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        setFragment(HomeFragment.newInstance());
                        break;
                    case R.id.action_pulse:
                        setFragment(HeartRateFragment.newInstance());
                        break;
                    case R.id.action_run:
                        setFragment(RunFragment.newInstance());
                        break;
                }
                return true;
            }
        });

        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                //
            }
        });

        setFragment(HomeFragment.newInstance());
    }

    private void setFragment(Fragment object) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layout_container, object);
        fragmentTransaction.commit();
    }

    private void setRepeatingAlarm(PendingIntent intent, AlarmManager manager, int hour, int min, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);

        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intent);
    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
