package com.example.fitty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.fitty.R;
import com.example.fitty.models.AppData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ChartPagerAdapter extends PagerAdapter {

    private Context context;

    public ChartPagerAdapter(Context context) {
        this.context = context;
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

        LineChart chart;
        ArrayList xData = new ArrayList<Entry>();
        ArrayList yData = new ArrayList();

        if (AppData.CHART_VIEW_IDS[position] == R.layout.view_step_chart) {
            chart = view.findViewById(R.id.view_step_chart);
            LineDataSet dataSet = new LineDataSet(getStepData(), "Steps");

            dataSet.setColor(ContextCompat.getColor(ChartPagerAdapter.this.context, R.color.colorPrimaryDark));
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            chart.getAxisRight().setDrawLabels(false);
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);

            chart.getDescription().setTextSize(12);
            chart.getDescription().setText("Your total steps count for each day.");
            chart.getLegend().setEnabled(false);

            LineData data = new LineData(dataSet);
            chart.setData(data);
            chart.invalidate();
        }

        container.addView(view);
        return view;
    }

    private ArrayList getStepData() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i=0; i<AppData.linechart_x.length; i++) {
            entries.add(new Entry(i, AppData.linechart_x[i]));
        }
        return entries;
    }
}
