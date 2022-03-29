package edu.neu.cs5520.alphaobserver.stockDetail;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import edu.neu.cs5520.alphaobserver.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WeekFragment extends Fragment {

    LineChart chart;

    TimePeriod timePeriod = TimePeriod.ONE_WEEK;

    public WeekFragment() {
        // Required empty public constructor
    }

    public void setChart(List<float[]> data) {

        chart = (LineChart) getActivity().findViewById(R.id.week_chart);
        ChartUtil.setChartAxis(chart);
        ChartUtil.setChartData(chart, data.subList(0, timePeriod.getNumberOfDays()), getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        return view;
    }
}