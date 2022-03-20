package edu.neu.cs5520.alphaobserver.companyInfo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;

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
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;

public class ChartUtil {


    public static void setChartAxis(LineChart chart) {
        XAxis xAxis;
        {   // // X-Axis Style // //
            xAxis = chart.getXAxis();

            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f);

            xAxis.setDrawLabels(false);

        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);

            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f);

            yAxis.setDrawLabels(false);

        }
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
            dataSet.setLineWidth(3f);

            // disable description text
            chart.getDescription().setEnabled(false);

            dataSet.setDrawIcons(false);

            // draw dashed line
            dataSet.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            dataSet.setColor(0xFFB48DD2);
            dataSet.setCircleColor(Color.BLACK);

            // line thickness and point size
            dataSet.setLineWidth(1f);
            dataSet.setCircleRadius(3f);

            // draw points as solid circles
            dataSet.setDrawCircleHole(false);

            // customize legend entry
            dataSet.setFormLineWidth(1f);
            dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            dataSet.setFormSize(15.f);

            // text size of values
            dataSet.setValueTextSize(12f);

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
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_purple);
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