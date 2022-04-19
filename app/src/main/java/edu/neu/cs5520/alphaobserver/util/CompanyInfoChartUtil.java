package edu.neu.cs5520.alphaobserver.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.security.keystore.StrongBoxUnavailableException;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;

public class CompanyInfoChartUtil {


    public static void setChartAxis(BarChart chart) {
        /*XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(false);
        leftAxis.enableGridDashedLine(10f, 10f, 10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);


        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);

        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setDrawAxisLine(false);*/
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);

        chart.setDrawValueAboveBar(false);
        chart.setHighlightFullBarEnabled(false);

        // change the position of the y-labels
        YAxis leftAxis = chart.getAxisLeft();

        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        chart.getAxisRight().setEnabled(false);

        XAxis xLabels =chart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.TOP);
    }

    public static void setChartData(final BarChart chart, List<float[]> data, Context context) {
        if (data.size() == 0) return;

        List<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < data.size(); i++) {

            // turn your data into Entry objects
            entries.add(new BarEntry(data.get(i)[0], data.get(i)[1]));
        }


        BarDataSet dataSet = new BarDataSet(entries, "Income Statement"); // add entries to dataset

        {  // // LineChart Styling  // //
            //dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            // Line Color and weight
            dataSet.setColor(0xFFB48DD2);

            // disable description text
            chart.getDescription().setEnabled(false);


            //dataSet.setDrawIcons(false);

            //dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            //dataSet.setDrawValues(true);

            //dataSet.setHighLightColor(Color.rgb(244, 117, 117));


        }

        chart.setNoDataText("WAITING FOR QUERY...");

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        chart.invalidate(); // refresh
    }


}