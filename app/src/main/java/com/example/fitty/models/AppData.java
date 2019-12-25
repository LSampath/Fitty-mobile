package com.example.fitty.models;

import com.example.fitty.R;

public class AppData {

    public static final String FIRST_TIME = "first_time_login";

    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String GENGER = "gender";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";

    public static final String RECEIVER_CODE = "code_de_receive";
    public static final int COUNTER_START_RECEIVER = 34;
    public static final int COUNTER_STOP_RECEIVER = 35;

    public static final int CHART_LIMIT = 30;
    public static final int[] CHART_VIEW_IDS = {
            R.layout.view_step_chart,
            R.layout.view_run_chart
    };


//    remove this
    public static double[] linechart_y = { 123, 3436, 2352, 2345, 2345, 6543, 3234, 1234, 3432, 1234, 2321};
    public static int[] linechart_x = { 1, 2, 4, 5, 5, 2, 5, 2, 4, 6, 6};
}
