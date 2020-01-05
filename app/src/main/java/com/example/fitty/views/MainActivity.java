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
import com.example.fitty.models.AppData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = this.getSharedPreferences(AppData.SHARED_PREF, MODE_PRIVATE);
//        if (preferences.getBoolean(AppData.FIRST_TIME, true)) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }


         //register broadcast receivers ////////////////////////////////////////////////////////////
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
        Intent counterStartAlarm = new Intent(this, AlarmReceiver.class);
        counterStartAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_START_RECEIVER);
        PendingIntent startIntent = PendingIntent.getBroadcast(this, 0, counterStartAlarm, 0);
        setRepeatingAlarm(startIntent, alarmManager, 23, 13, 0);

        Intent counterStopAlarm = new Intent(this, AlarmReceiver.class);
        counterStopAlarm.putExtra(AppData.RECEIVER_CODE, AppData.COUNTER_STOP_RECEIVER);
        PendingIntent stopIntent = PendingIntent.getBroadcast(this, 1, counterStopAlarm, 0);
        setRepeatingAlarm(stopIntent, alarmManager, 23, 17, 30);

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
                    case R.id.action_history:
                        setFragment(HistoryFragment.newInstance());
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

//        remove this
//        DatabaseHelper db = new DatabaseHelper(this);
//        SleepController.insertHours(db, 12.34);
//        SleepController.insertHours(db, 5.9);
//        SleepController.insertHours(db, 8.38);


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

//        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);

        Log.i("TIME", "..............................................HOUR " + calendar.get(Calendar.HOUR_OF_DAY));
        Log.i("TIME", "..............................................MINUTE " + calendar.get(Calendar.MINUTE));
        Log.i("TIME", "..............................................SECOND " + calendar.get(Calendar.SECOND));

        Log.i("TIME", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + calendar.getTimeInMillis());
        Log.i("TIME", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + SystemClock.elapsedRealtime());

//        calendar.add(Calendar.SECOND, sec);

        manager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intent);
    }
}
