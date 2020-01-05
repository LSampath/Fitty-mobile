package com.example.fitty.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitty.R;
import com.example.fitty.adapters.DatabaseHelper;
import com.example.fitty.controllers.RunController;
import com.example.fitty.models.AppData;
import com.example.fitty.models.RunningSession;
import com.example.fitty.services.RunTrackerService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, LocationListener {

    private MapView mapView;
    private LocationManager locationManager;
    private GoogleMap googleMap;
    private Button startBtn;
    private Button stopBtn;
    private Button pauseBtn;
    private TextView distance_val, speed_val;
    private Chronometer time_val;

    private ConstraintLayout initLayout;
    private ConstraintLayout activeLayout;
    private ConstraintLayout finalLayout;

    private Button saveBtn;
    private Button discardBtn;
    private TextView speedView;
    private TextView durationView;
    private TextView distanceView;

    // Polyline Styling
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xffb32bed;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final int POLYLINE_STROKE_WIDTH_PX = 5;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    public static final int ACCURACY_LEVEL = 5;
    private static final int RESPONSIVENESS = 5000;
    private Timer timerTask;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private int fragment_state;
    private Polyline polyline;

    public static boolean isGPSEnabled;
    public static boolean isNetworkEnabled;

    public static ArrayList<LatLng> arrayList;
    private LatLng initialPosition;
    private boolean initialPositionCheckedThroughLastLocation;

    public static boolean timeRunning;
    public static double distance;
    private long pauseOffset;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean initialPositionChecked;
    private Intent getAlarmIntent;
    private Handler handler;


    public RunFragment() {
        this.fragment_state = AppData.RUN_INIT_STATE;
    }

    public static RunFragment newInstance() {
        RunFragment fragment = new RunFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        super.onCreate(savedInstanceState);
    }

    public void initialize(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler();
        timerTask = new Timer();

        //getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        timeRunning = false;
        getAlarmIntent = new Intent(getActivity(), RunTrackerService.class);

        initialPositionChecked = false;
        initialPositionCheckedThroughLastLocation = false;
        distance = 0;
        arrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_run, container, false);

        mapView = view.findViewById(R.id.fragment_run_map);

        startBtn = view.findViewById(R.id.fragment_run_btn_start);
        stopBtn = view.findViewById(R.id.fragment_run_btn_stop);
        pauseBtn = view.findViewById(R.id.fragment_run_btn_pause);
        distance_val = view.findViewById(R.id.fragment_run_tv_distance);
        time_val = view.findViewById(R.id.fragment_run_tv_time);
        speed_val = view.findViewById(R.id.fragment_run_tv_speed);
        timeRunning = false;

        // final layout view initialization
        saveBtn = view.findViewById(R.id.fragment_run_btn_save);
        discardBtn = view.findViewById(R.id.fragment_run_btn_discard);
        speedView = view.findViewById(R.id.fragment_run_tv_speed_2);
        durationView = view.findViewById(R.id.fragment_run_tv_duration_2);
        distanceView = view.findViewById(R.id.fragment_run_tv_distance_2);

        initLayout = view.findViewById(R.id.fragment_run_con_init);
        activeLayout = view.findViewById(R.id.fragment_run_con_active);
        finalLayout = view.findViewById(R.id.fragment_run_con_final);

        // only init layout is visible
        fragment_state = AppData.RUN_INIT_STATE;
//        updateLayout();

        initialize();

        initGoogleMap(savedInstanceState);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            initialPosition = new LatLng(location.getLatitude(),location.getLongitude());
                            initialPositionCheckedThroughLastLocation = true;
                        }
                    }
                });


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment_state == AppData.RUN_INIT_STATE) {

                    // visible active layout
                    fragment_state = AppData.RUN_ACTIVE_STATE;
                    updateLayout();

                    resetText();
                    if(!timeRunning){
                        time_val.setBase(SystemClock.elapsedRealtime());
                        time_val.start();

                        timeRunning = true;
                        startTracking();
//                        time_val.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//                            @Override
//                            public void onChronometerTick(Chronometer chronometer) {
//                                // Logic
//                            }
//                        });
                    }
                }
            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeRunning){
                    time_val.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - time_val.getBase();
                    timeRunning = false;
                    pauseBtn.setText("Start");
                } else {
                    time_val.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    time_val.start();
                    timeRunning = true;
                    pauseBtn.setText("Pause");
                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment_state == AppData.RUN_ACTIVE_STATE) {

                    //stop timer to update map
                    timerTask.cancel();

                    //stop run tracking service
                    getActivity().stopService(getAlarmIntent);
                    initialize();

                    fragment_state = AppData.RUN_FINAL_STATE;
                    updateLayout();

                    speedView.setText(speed_val.getText().toString());
                    durationView.setText((SystemClock.elapsedRealtime() - time_val.getBase())/(1000) + " SEC");
                    distanceView.setText(distance_val.getText().toString());
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String distanceStr = distanceView.getText().toString();
                float distance = Float.parseFloat(distanceStr.substring(0, distanceStr.length()-3));

                String durationStr = durationView.getText().toString();
                long duration = Long.parseLong(durationStr.substring(0, durationStr.length()-4)) * 1000;

                long startTime = time_val.getBase();
                long endTime = time_val.getBase() + duration;

                DatabaseHelper db = new DatabaseHelper(getContext());
                RunController.insertSession(db, new RunningSession(startTime, endTime, distance));
                Toast.makeText(getContext(), "Saved to database", Toast.LENGTH_SHORT).show();
            }
        });

        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_state = AppData.RUN_INIT_STATE;
                updateLayout();
            }
        });

        return view;
    }

    private void startTracking() {
        getActivity().startService(getAlarmIntent);

        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateMap();
                    }
                });
            }
        }, 0, RESPONSIVENESS);

    }

    public void updateMap(){
        if(initialPositionChecked){
            if(timeRunning){
                polyline.setPoints(arrayList);
                polyline.setTag("A");
                stylePolyline(polyline);
                distance_val.setText(round(distance,2) + " KM");
                //speed
                Long currentTimeInSeconds = (SystemClock.elapsedRealtime() - time_val.getBase())/(1000);
                Double speed = (distance*3600)/currentTimeInSeconds;
                speed_val.setText(String.format("%.2f", speed) + " KMPH");
            }
        } else if (initialPositionCheckedThroughLastLocation){
                googleMap.addMarker(new MarkerOptions().position(new LatLng(initialPosition.latitude, initialPosition.longitude)).title("Starting Point"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialPosition.latitude, initialPosition.longitude), 15));
                arrayList.add(initialPosition);
                initialPositionChecked = true;
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request Permission
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            return;
        }

        if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1 , this);
        }
        if(isGPSEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }

        checkStatus();
    }

    private void checkStatus() {
        if(!isGPSEnabled){
            distance_val.setText("GPS offline");
            speed_val.setText("GPS offline");
        }
        else{
            distance_val.setText("Checking");
            speed_val.setText("Checking");
        }
    }

    public void resetText(){
        distance_val.setText("0 KM");
        time_val.setText("0 SEC");
        speed_val.setText("0 KMPH");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().stopService(getAlarmIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        getActivity().stopService(getAlarmIntent);
        initialize();
//        RunFragment.this.runActive = false;
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(getActivity(), "Route Track ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        if (!initialPositionCheckedThroughLastLocation && !initialPositionChecked){
                initialPosition = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(new LatLng(initialPosition.latitude, initialPosition.longitude)).title("Starting Point"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialPosition.latitude, initialPosition.longitude), 15));
                initialPositionChecked = true;
                arrayList.add(initialPosition);
                locationManager.removeUpdates(this);
                locationManager = null;
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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

    private void stylePolyline(Polyline polyline) {
        String type = "";

        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }
        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(new RoundCap());
                polyline.setEndCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

//        polyline.setStartCap(new CustomCap(
//                BitmapDescriptorFactory.fromResource(R.drawable.square), 15));
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_PURPLE_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        polyline = googleMap.addPolyline(new PolylineOptions().clickable(true));
        googleMap.setOnPolylineClickListener(this);
    }

    private void updateLayout() {
        initLayout.setVisibility(View.INVISIBLE);
        activeLayout.setVisibility(View.INVISIBLE);
        finalLayout.setVisibility(View.INVISIBLE);

        int layout_id = 0;

        if (this.fragment_state == AppData.RUN_INIT_STATE) {
            initLayout.setVisibility(View.VISIBLE);
            layout_id = R.id.fragment_run_con_init;
        } else if (this.fragment_state == AppData.RUN_ACTIVE_STATE) {
            activeLayout.setVisibility(View.VISIBLE);
            layout_id = R.id.fragment_run_con_active;
        } else if (this.fragment_state == AppData.RUN_FINAL_STATE) {
            finalLayout.setVisibility(View.VISIBLE);
            layout_id = R.id.fragment_run_con_final;
        }

        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layout;

        layout = getActivity().findViewById(R.id.fragment_run_container_main);
        set.clone(layout);
        set.clear(R.id.fragment_run_container, ConstraintSet.BOTTOM);
        set.connect(R.id.fragment_run_container, ConstraintSet.BOTTOM, layout_id, ConstraintSet.TOP,0);
        set.applyTo(layout);
    }

}
