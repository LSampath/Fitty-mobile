package com.example.fitty.views;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitty.R;
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
import com.google.android.gms.maps.model.SquareCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private TextView distance_val, time_val, speed_val;

    private ConstraintLayout startLayout;
    private ConstraintLayout runLayout;

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

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private boolean runActive;
    private Polyline polyline;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    private ArrayList<LatLng> arrayList;
    private ArrayList<LatLng> temp;
    private LatLng initialPosition;
    private boolean initialPositionChecked = false;

    private int i;
    private int j;
    private double distance = 0;

    public RunFragment() {
        this.runActive = false;
    }

    public static RunFragment newInstance() {
        RunFragment fragment = new RunFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        View view = inflater.inflate(R.layout.fragment_run, container, false);

        mapView = view.findViewById(R.id.fragment_run_map);

        startBtn = view.findViewById(R.id.fragment_run_btn_start);
        stopBtn = view.findViewById(R.id.fragment_run_btn_stop);
        pauseBtn = view.findViewById(R.id.fragment_run_btn_pause);
        distance_val = view.findViewById(R.id.fragment_run_tv_distance);
        time_val = view.findViewById(R.id.fragment_run_tv_time);
        speed_val = view.findViewById(R.id.fragment_run_tv_speed);

        startLayout = view.findViewById(R.id.fragment_run_con_start);
        runLayout = view.findViewById(R.id.fragment_run_con_run);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!RunFragment.this.runActive) {
                    RunFragment.this.runActive = true;
                    startLayout.setVisibility(View.INVISIBLE);
                    runLayout.setVisibility(View.VISIBLE);
                    resetText();
                }
            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RunFragment.this.runActive) {
                    RunFragment.this.runActive = false;
                    startLayout.setVisibility(View.VISIBLE);
                    runLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        initGoogleMap(savedInstanceState);

        i = 0;
        j = 0;
        temp = new ArrayList<>();
        arrayList = new ArrayList<>();

        return view;
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
        time_val.setText("0 Min 0 Sec");
        speed_val.setText("0 KMPH");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        Toast.makeText(getActivity(), "Route Track " + polyline.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        if (i>0 && !initialPositionChecked){
            initialPosition = new LatLng(location.getLatitude(),location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(new LatLng(initialPosition.latitude, initialPosition.longitude)).title("Starting Point"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialPosition.latitude, initialPosition.longitude), 15));
            initialPositionChecked = true;
            arrayList.add(initialPosition);
        } else if(i<1){
            i = i + 1;
        } else {
            double avg_lat = 0;
            double avg_lon = 0;
            j = j + 1;
            temp.add(new LatLng(location.getLatitude(),location.getLongitude()));
            speed_val.setText(Integer.toString(j));
            if(j==10){
                for (LatLng latLng: temp){
                    avg_lat = avg_lat + latLng.latitude;
                    avg_lon = avg_lon + latLng.longitude;
                }
                avg_lat = avg_lat/temp.size();
                avg_lon = avg_lon/temp.size();
                LatLng newPoint = new LatLng(avg_lat,avg_lon);
                temp.clear();
                distance = distance + round(harversineDistance(arrayList.get(arrayList.size()-1),newPoint),2);
                arrayList.add(newPoint);
                distance_val.setText(round(distance,2) + " KM");
                j = 0;
                polyline.setPoints(arrayList);
                polyline.setTag("A");
                stylePolyline(polyline);
            }
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

    public Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        polyline = googleMap.addPolyline(new PolylineOptions().clickable(true));
        googleMap.setOnPolylineClickListener(this);
    }

}
