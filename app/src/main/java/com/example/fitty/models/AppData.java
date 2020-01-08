package com.example.fitty.models;

import android.Manifest;

import com.example.fitty.R;

public class AppData {

    public static final String SHARED_PREF = "account_preferences";

    public static final String FIRST_TIME = "first_time_login";

    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String GENDER = "gender";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";

    public static final int RUN_INIT_STATE = 1;
    public static final int RUN_ACTIVE_STATE = 2;
    public static final int RUN_FINAL_STATE = 3;

    public static final String RECEIVER_CODE = "code_de_receive";
    public static final int COUNTER_START_RECEIVER = 134;
    public static final int COUNTER_STOP_RECEIVER = 135;
    public static final int SLEEP_START_RECEIVER = 136;
    public static final int SLEEP_STOP_RECEIVER = 137;

    public static final int CHART_LIMIT = 30;
    public static final int[] CHART_VIEW_IDS = {
            R.layout.view_step_chart,
            R.layout.view_run_chart,
            R.layout.view_sleep_chart
    };

    public static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.INTERNET,
    };

    public static final double SLEEP_LOWER_BOUND = 7;
    public static final int STEPS_LOWER_BOUND = 11000;
}
