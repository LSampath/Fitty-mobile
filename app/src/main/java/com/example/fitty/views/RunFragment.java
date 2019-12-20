package com.example.fitty.views;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fitty.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;

    private Button startBtn;
    private Button stopBtn;
    private Button pauseBtn;

    private ConstraintLayout startLayout;
    private ConstraintLayout runLayout;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private boolean runActive;


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
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        mapView = view.findViewById(R.id.fragment_run_map);

        startBtn = view.findViewById(R.id.fragment_run_btn_start);
        stopBtn = view.findViewById(R.id.fragment_run_btn_stop);
        pauseBtn = view.findViewById(R.id.fragment_run_btn_pause);

        startLayout = view.findViewById(R.id.fragment_run_con_start);
        runLayout = view.findViewById(R.id.fragment_run_con_run);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! RunFragment.this.runActive) {
                    RunFragment.this.runActive = true;
                    startLayout.setVisibility(View.INVISIBLE);
                    runLayout.setVisibility(View.VISIBLE);
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

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
    }
}
