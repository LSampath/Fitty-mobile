package com.example.fitty.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fitty.adapters.ChartPagerAdapter;
import com.example.fitty.R;
import com.example.fitty.models.AppData;
import com.example.fitty.models.StepCount;
import com.example.fitty.services.StepCountService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private ViewPager pager;
    private TextView stepView;
    private TextView calories;
    private BroadcastReceiver broadcastReceiver;
    private StepCount stepCount;
    private int todayCount = 0;

    private float weight;
    private float height;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences(AppData.SHARED_PREF, 0);
        this.weight = preferences.getFloat(AppData.WEIGHT, 0);
        this.height = preferences.getFloat(AppData.HEIGHT, 0);

        this.pager = view.findViewById(R.id.fragment_home_pager);
        this.pager.setAdapter(new ChartPagerAdapter(HomeFragment.this.getContext()));

        stepView = (TextView) view.findViewById(R.id.fragment_home_tv_steps);
        calories = (TextView) view.findViewById(R.id.fragment_home_tv_calory);
//        startService(view);
        return view;
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
    public void onResume() {

        super.onResume();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                todayCount = intent.getIntExtra("step_value",0);
//                getStepCount().setCount(todayCount);
                stepView.setText("" + todayCount);
                calories.setText("" + (float) StepCount.getCalories(todayCount, weight, height));
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("new_step_taken"));
    }

    public void startService(View v) {
        Intent serviceIntent = new Intent(v.getContext(), StepCountService.class);
        getActivity().startService(serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(v.getContext(), StepCountService.class);
        getActivity().stopService(serviceIntent);
    }

}
