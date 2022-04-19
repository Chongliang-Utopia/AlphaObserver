package edu.neu.cs5520.alphaobserver.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.service.CompanyInfoService;
import edu.neu.cs5520.alphaobserver.util.CompanyInfoChartUtil;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class GrossCostFragment extends Fragment {

    BarChart chart;


    private static final String INCREASE_GREEN = "#FF4CAF50";
    private static final String DECREASE_RED = "#FFF44336";

    public GrossCostFragment() {
        // Required empty public constructor
    }

    public void setChart(List<float[]> data, Activity activity) {
        if (activity == null) return;
        chart = (BarChart) activity.findViewById(R.id.cost_chart);

        if (chart != null && data.size() > 0) {
            setChartHelper(data, chart, activity);
        }

    }
    private void setChartHelper(List<float[]> data, BarChart chart, Activity activity) {

        CompanyInfoChartUtil.setChartAxis(chart);
        CompanyInfoChartUtil.setChartData(chart, data, activity);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gross_cost, container, false);

        // Call when this is not the first Tab to open, so the data is already in stockService.java.

        chart = (BarChart) view.findViewById(R.id.cost_chart);
        List<float[]> data = CompanyInfoService.getCostData();

        if (chart != null && data != null && data.size() > 0) {
            setChartHelper(data, chart, getActivity());
        }

        return view;
    }
}