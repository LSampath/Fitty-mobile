package com.example.fitty.views;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.fitty.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RunTrackerService extends Service implements LocationListener {

    private int j;
    private ArrayList<LatLng> temp;
    private LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        temp = new ArrayList<>();
        j = 0;
        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        //Permission Request procedure is implemented in RunFragment
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            return START_STICKY;
        }

        if(RunFragment.isGPSEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
        if(RunFragment.isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1 , this);
        }

        // Notification
        String NOTIFICATION_CHANNEL_ID = "com.example.FITTY";
        String channelName = "Run Tracker Background Service";
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    public Double harversineDistance(LatLng pointA, LatLng pointB){
        // Radious of the earth
        final int R = 6371;
        Double lat1 = pointA.latitude;
        Double lon1 = pointA.longitude;
        Double lat2 = pointB.latitude;
        Double lon2 = pointB.longitude;
        Double latDistance = toRad(lat2-lat1);
        Double lonDistance = toRad(lon2-lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R * c;
        return distance;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(RunFragment.timeRunning){
            double avg_lat = 0;
            double avg_lon = 0;
            j = j + 1;
            temp.add(new LatLng(location.getLatitude(), location.getLongitude()));
            if (j == RunFragment.ACCURACY_LEVEL) {
                j = 0;
                for (LatLng latLng : temp) {
                    avg_lat = avg_lat + latLng.latitude;
                    avg_lon = avg_lon + latLng.longitude;
                }
                avg_lat = avg_lat / RunFragment.ACCURACY_LEVEL;
                avg_lon = avg_lon / RunFragment.ACCURACY_LEVEL;
                LatLng newPoint = new LatLng(avg_lat, avg_lon);
                temp.clear();
                RunFragment.distance = RunFragment.distance + round(harversineDistance(RunFragment.arrayList.get(RunFragment.arrayList.size() - 1), newPoint), 2);
                RunFragment.arrayList.add(newPoint);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
