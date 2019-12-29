package com.example.fitty.views;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.fitty.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SleepDetectorService extends Service {

    private SensorManager sensorManager;

    private Sensor accelerometer;
    private Sensor ambLight;

    private Date lastUsed;
    private Date lastIdle;
    private boolean isSleeping;
    private boolean dateSet;
    private String currentDay;
    private static final int  MIN_SLEEPING_HOURS = 4;
    private static final int  MAX_SLEEPING_HOURS = 11;
    private static final Float MAX_LIGHT_SUITABLE = 30.0f;
    private Date sleepStart;
    private long lastSleep;
    private long bestSleep;
    private Date sleepEnd;
    private float currentLight;
    private SensorEventListener sensorEventListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();

        // Notification
        String NOTIFICATION_CHANNEL_ID = "com.example.FITTY";
        String channelName = "Sleep Detection Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.heart)
                .setContentTitle("FITTY is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    public void initialize() {
        currentLight = 0;
        lastSleep = 0L;
        bestSleep = 0L;
        currentDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        if(lastIdle == null){
            try {
                lastIdle = defaultDateFormat.parse(currentTime);
                lastUsed = defaultDateFormat.parse(currentTime);
            } catch (ParseException e) {
                //Exception
            }
        }

        isSleeping = false;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager!=null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            ambLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    checkSleep(event);
                }
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    updateLightConditions(event.values[0]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, ambLight, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void updateLightConditions(float value) {
        currentLight = value;
    }

    public boolean lightConditionsSuitable(){
        if(currentLight>MAX_LIGHT_SUITABLE){
            return false;
        }
        return true;
    }

    public void checkSleep(SensorEvent event) {

        // Date setting
        if(!dateSet){
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String dayBegining =  new SimpleDateFormat("12:00:00", Locale.getDefault()).format(new Date());
            String justAfterDayBegining =  new SimpleDateFormat("12:10:00", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("H:mm:ss", Locale.getDefault()).format(new Date());
            try {
                Date current = timeFormat.parse(currentTime);
                Date rightAt12 = timeFormat.parse(dayBegining);
                Date bitAfter12 = timeFormat.parse(justAfterDayBegining);
                if (current.compareTo(rightAt12)>=0 && bitAfter12.compareTo(current) >= 0) {
                    // It's between 12 noon and 12.10pm
                    dateSet = true;

                    currentDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                }
            } catch (ParseException e) {
                // Exception
            }
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String justBeforeDayBegining =  new SimpleDateFormat("11:50:00", Locale.getDefault()).format(new Date());
            String dayBegining =  new SimpleDateFormat("12:00:00", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("H:mm:ss", Locale.getDefault()).format(new Date());
            try {
                Date current = timeFormat.parse(currentTime);
                Date justBefore12 = timeFormat.parse(justBeforeDayBegining);
                Date at12 = timeFormat.parse(dayBegining);
                if (current.compareTo(justBefore12)>=0 && at12.compareTo(current) >= 0) {
                    // It's between 11.59am and 12 noon
                    dateSet = false;

                    // TODO
                    // Send Data to database
                    // reset variables
                }
            } catch (ParseException e) {
                // Exception
            }
        }

        if (isPhoneOrientationHorizontal(event.values[0], event.values[1], event.values[2])) {
            if (!isPhoneTouched()) {
                if (!phoneIdleDetected()) {
                    // Not updated ==> Still sleeping
                    String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                    try {
                        Date current = defaultDateFormat.parse(currentTime);
                        if ((current.getTime() - lastIdle.getTime()) / 1000 >= MIN_SLEEPING_HOURS * 3600) {
                            if((current.getTime() - lastIdle.getTime()) / 1000 <= MAX_SLEEPING_HOURS * 3600){
                                isSleeping = true;
                                // TODO
                                // compare with last sleeping values and update sleepStart
                                if(((current.getTime() - lastIdle.getTime()) / 1000) > bestSleep){
                                    sleepStart = lastIdle;
                                    sleepEnd = current;
                                }
                            }
//
                        }
                    } catch (ParseException e) {
                            // Exception
                    }
                }
            } else {
                phoneUseDetected();
            }
        } else {
            phoneUseDetected();
        }
    }


    public boolean isPhoneOrientationHorizontal(Float x, Float y, Float z) {
        if (Math.abs(z) >= 9.5 && Math.abs(z) <= 10.5 && Math.abs(y) >= -1 && Math.abs(y) <= 1) {
            System.out.println("Phone is kept on a surface");
            return true;
        } else if (isPhonePicked(y, z)) {
            return false;
        } else if (isPhoneRotatingMoving(x)) {
            return false;
        }
        System.out.println("Indeterminate phone orientation");
        return true;
    }

    public boolean isPhonePicked(Float y, Float z) {
        if (Math.abs(z) <= 9.5 && Math.abs(y) >= 2.5) {
            System.out.println("Phone is picked");
            return true;
        }
        return false;
    }

    public boolean isPhoneRotatingMoving(Float x) {
        if (Math.abs(x) > 1) {
            System.out.println("Phone is probably being moved intentionally");
            return true;
        }
        return false;
    }

    public boolean isPhoneTouched() {
        //TODO
        return false;
    }

    public void phoneUseDetected() {
        if (lastIdle.compareTo(lastUsed) >= 0) {
            //Idle until now
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            try {
                lastUsed = defaultDateFormat.parse(currentTime);
                if (isSleeping) {
                    lastSleep = (lastUsed.getTime() - lastIdle.getTime()) / 1000;
                    if(lastSleep >= bestSleep){
                        bestSleep = lastSleep;
                        sleepStart = lastIdle;
                        sleepEnd = lastUsed;
                    }
                    isSleeping = false;
                }
            } catch (ParseException e) {
                //Exception
            }
        }
    }

    public boolean phoneIdleDetected() {
        if (lastUsed.compareTo(lastIdle) >= 0) {
            //Being used until now
            if(lightConditionsSuitable()){
                String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                try {
                    lastIdle = defaultDateFormat.parse(currentTime);
                    //last_slept.setText(currentTime);
                    return true;
                } catch (ParseException e) {
                    //Exception
                }
            }
            // Not having conditions to start sleeping
            return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
