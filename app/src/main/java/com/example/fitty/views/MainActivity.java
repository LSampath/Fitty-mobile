package com.example.fitty.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.fitty.services.TestService;
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
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//
//            DatabaseHelper db = new DatabaseHelper(this);
//            SleepController.insertHours(db, 12.34);
//            SleepController.insertHours(db, 5.9);
//            SleepController.insertHours(db, 8.38);
//
//            StepController.insertCount(db, 8200);
//            StepController.insertCount(db, 12300);
//            StepController.insertCount(db, 6400);
//
//            RunController.insertSession(db, new RunningSession(1578242925884L, 1578253725884L, 12.34));
//            RunController.insertSession(db, new RunningSession(1578242925884L, 1578248325884L, 8.54));
//            RunController.insertSession(db, new RunningSession(1578242925884L, 1578250125884L, 6.90));

            Intent intentStep = new Intent(this, StepCountService.class);
            this.startService(intentStep);

            Intent intentSleep = new Intent(this, SleepDetectorService.class);
            this.startService(intentSleep);

//            Intent test = new Intent(this, TestService.class);
//            this.startService(test);
        }


         //register broadcast receivers ////////////////////////////////////////////////////////////
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Step counter service
        Intent counterStartAlarm = new Intent(this, AlarmReceiver.class);
        counterStartAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_START_RECEIVER);
        PendingIntent counterStartIntent = PendingIntent.getBroadcast(this, 0, counterStartAlarm, 0);
        setRepeatingAlarm(counterStartIntent, alarmManager, 23, 55, 0);

        Intent counterStopAlarm = new Intent(this, AlarmReceiver.class);
        counterStopAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_STOP_RECEIVER);
        PendingIntent counterStopIntent = PendingIntent.getBroadcast(this, 1, counterStopAlarm, 0);
        setRepeatingAlarm(counterStopIntent, alarmManager, 0, 05, 0);

        // Sleep detector service
        Intent sleepStartAlarm = new Intent(this, AlarmManager.class);
        sleepStartAlarm.putExtra(AppData.RECEIVER_CODE, AppData.SLEEP_START_RECEIVER);
        PendingIntent sleepStartIntent = PendingIntent.getBroadcast(this, 2, sleepStartAlarm, 0);
        setRepeatingAlarm(sleepStartIntent, alarmManager, 18, 0, 0);

        Intent sleepStopAlarm = new Intent(this, AlarmManager.class);
        sleepStopAlarm.putExtra(AppData.RECEIVER_CODE, AppData.SLEEP_STOP_RECEIVER);
        PendingIntent sleepStopIntent = PendingIntent.getBroadcast(this, 3, sleepStopAlarm, 0);
        setRepeatingAlarm(sleepStopIntent, alarmManager, 17, 55, 0);

        // //////////////////////////////////////////////////////////////////////////////////////////

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
}
