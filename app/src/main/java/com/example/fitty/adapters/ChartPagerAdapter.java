package com.example.fitty.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.fitty.R;
import com.example.fitty.controllers.RunController;
import com.example.fitty.controllers.SleepController;
import com.example.fitty.controllers.StepController;
import com.example.fitty.models.AppData;
import com.example.fitty.models.RunningSession;
import com.example.fitty.models.SleepHours;
import com.example.fitty.models.StepCount;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class ChartPagerAdapter extends PagerAdapter {

    private Context context;
    private DatabaseHelper db;

    public ChartPagerAdapter(Context context) {
        this.context = context;
        this.db = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return AppData.CHART_VIEW_IDS.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(AppData.CHART_VIEW_IDS[position], container, false);

        LineChart chart = null;
        ArrayList<ILineDataSet> multiple = new ArrayList<>();

        if (AppData.CHART_VIEW_IDS[position] == R.layout.view_step_chart) {
            chart = view.findViewById(R.id.view_step_chart);

            LineDataSet dataSet = new LineDataSet(getStepEntries(), "Daily Steps");
            dataSet.setColor(ContextCompat.getColor(ChartPagerAdapter.this.context, R.color.chart_1));
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            multiple.add(dataSet);

        } else if (AppData.CHART_VIEW_IDS[position] == R.layout.view_run_chart) {
            chart = view.findViewById(R.id.view_run_chart);

            LineDataSet distanceDataset = new LineDataSet(getDistanceEntries(), "Distance");
            distanceDataset.setColor(ContextCompat.getColor(ChartPagerAdapter.this.context, R.color.chart_1));
            distanceDataset.setDrawValues(false);
            distanceDataset.setDrawCircles(false);
            distanceDataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            multiple.add(distanceDataset);

            LineDataSet speedDataset = new LineDataSet(getSpeedEntries(), "Avg Speed");
            speedDataset.setColor(ContextCompat.getColor(ChartPagerAdapter.this.context, R.color.chart_2));
            speedDataset.setDrawValues(false);
            speedDataset.setDrawCircles(false);
            speedDataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            multiple.add(speedDataset);

        } else if (AppData.CHART_VIEW_IDS[position] == R.layout.view_sleep_chart) {
            chart = view.findViewById(R.id.view_sleep_chart);

            LineDataSet dataSet = new LineDataSet(getSleepEntries(), "Sleeping Hours");
            dataSet.setColor(ContextCompat.getColor(ChartPagerAdapter.this.context, R.color.chart_1));
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            multiple.add(dataSet);

        }

        LineData data = new LineData(multiple);
        chart.setData(data);

        chart.getAxisRight().setDrawLabels(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);

        chart.getDescription().setTextSize(12);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(true);

        chart.invalidate();

        container.addView(view);
        return view;
    }

    private ArrayList getStepEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<StepCount> counts = StepController.getCounts(this.db);

        int n = counts.size();
        for (int i=0; i<n; i++) {
            entries.add(new Entry(i, counts.get(i).getCount()));
        }
        return entries;
    }

    private ArrayList getDistanceEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<RunningSession> sessions = RunController.getSessions(this.db);

        int n = sessions.size();
        for (int i=0; i<n; i++) {
            entries.add(new Entry(i, (float) sessions.get(i).getDistance()));
        }
        return entries;
    }

    private ArrayList getSpeedEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<RunningSession> counts = RunController.getSessions(this.db);

        int n = counts.size();
        for (int i=0; i<n; i++) {
            entries.add(new Entry(i, (float) counts.get(i).getAvgSpeed()));
            Log.i("RUNNING", counts.get(i).getAvgSpeed()+"");
        }
        return entries;
    }

    private ArrayList getSleepEntries() {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<SleepHours> counts = SleepController.getHours(this.db);

        int n = counts.size();
        for (int i=0; i<n; i++) {
            entries.add(new Entry(i, (float) counts.get(i).getHours()));
        }
        return entries;
    }
}
