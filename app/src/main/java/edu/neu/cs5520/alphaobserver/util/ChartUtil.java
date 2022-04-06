package edu.neu.cs5520.alphaobserver.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.security.keystore.StrongBoxUnavailableException;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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

public class ChartUtil {


    public static void setChartAxis(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);


        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);

        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setDrawAxisLine(false);
    }

    public static void setChartData(final LineChart chart, List<float[]> data, Context context) {
        if (data.size() == 0) return;

        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < data.size(); i++) {

            // turn your data into Entry objects
            entries.add(new Entry(data.get(i)[0], data.get(i)[1]));
        }


        LineDataSet dataSet = new LineDataSet(entries, "Stock Price in $"); // add entries to dataset

        {  // // LineChart Styling  // //
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

            // Line Color and weight
            dataSet.setColor(0xFFB48DD2);
            dataSet.setLineWidth(2f);

            // disable description text
            chart.getDescription().setEnabled(false);

            dataSet.setDrawIcons(false);

            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setFillAlpha(65);
            dataSet.setFillColor(ColorTemplate.getHoloBlue());
            dataSet.setHighLightColor(Color.rgb(244, 117, 117));
            dataSet.setDrawCircleHole(false);

            // draw selection line as dashed
            dataSet.enableDashedHighlightLine(10f, 5f, 0f);


            // set the filled area
            dataSet.setDrawFilled(true);
            dataSet.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_pueple);
                dataSet.setFillDrawable(drawable);
            } else {
                dataSet.setFillColor(Color.BLACK);
            }
        }

        chart.setNoDataText("WAITING FOR QUERY...");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate(); // refresh
    }


}