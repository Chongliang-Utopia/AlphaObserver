package edu.neu.cs5520.alphaobserver.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.TimePeriod;
import edu.neu.cs5520.alphaobserver.service.StockService;
import edu.neu.cs5520.alphaobserver.util.ChartUtil;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MonthFragment extends Fragment {

    LineChart chart;

    TimePeriod timePeriod = TimePeriod.ONE_MONTH;

    public MonthFragment() {
        // Required empty public constructor
    }

    public void setChart(List<float[]> data, Activity activity) {
        if (activity == null) return;
        chart = (LineChart) activity.findViewById(R.id.month_chart);
        if (chart == null) return;
        ChartUtil.setChartAxis(chart);
        ChartUtil.setChartData(chart, data.subList(data.size()-timePeriod.getNumberOfDays(), data.size()), activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month, container, false);

        chart = (LineChart) view.findViewById(R.id.month_chart);
        if (chart == null) return view;
        ChartUtil.setChartAxis(chart);
        List<float[]> data = StockService.getData();
        Activity activity = StockService.getAct();
        if (data == null) return view;
        ChartUtil.setChartData(chart, data.subList(data.size()-timePeriod.getNumberOfDays(), data.size()), activity);

        return view;
    }
}