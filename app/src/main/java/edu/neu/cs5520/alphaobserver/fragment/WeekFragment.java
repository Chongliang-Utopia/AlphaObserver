package edu.neu.cs5520.alphaobserver.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.TimePeriod;
import edu.neu.cs5520.alphaobserver.service.StockService;
import edu.neu.cs5520.alphaobserver.util.ChartUtil;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WeekFragment extends Fragment implements IStockDetailChart {

    LineChart chart;

    TimePeriod timePeriod = TimePeriod.ONE_WEEK;

    private static final String INCREASE_GREEN = "#FF4CAF50";
    private static final String DECREASE_RED = "#FFF44336";

    public WeekFragment() {
        // Required empty public constructor
    }

    public void setChart(List<float[]> data, Activity activity) {
        if (activity == null) return;
        chart = (LineChart) activity.findViewById(R.id.week_chart);

        if (chart != null && data.size() > 0) {
            setChartHelper(data, chart, activity);
        }

        TextView percentageView = activity.findViewById(R.id.stockPercentage_week);
        setPercentage(data, percentageView);

        TextView priceView = activity.findViewById(R.id.stockPrice_week);
        setPrice(String.valueOf(data.get(data.size()-1)[1]), priceView);
    }

    @Override
    public void setPrice(String price, Activity activity) {
        setPrice(String.valueOf(price), activity.findViewById(R.id.stockPrice_week));
    }

    @Override
    public void setPercentage(List<float[]> data, Activity activity) {
        setPercentage(data, activity.findViewById(R.id.stockPercentage_week));
    }

    private void setChartHelper(List<float[]> data, LineChart chart, Activity activity) {

        ChartUtil.setChartAxis(chart);
        ChartUtil.setChartData(chart, data.subList(data.size()-timePeriod.getNumberOfDays(), data.size()), activity);

    }


    public void setCurrency(String currency, TextView currencyView) {

        if (currency == null) return;
        Currency stockCurrency = Currency.getInstance(currency);
        String stockCurrencySymbol = stockCurrency.getSymbol();
        if (currencyView != null)
            currencyView.setText(stockCurrencySymbol);
    }

    public void setPrice(String price, TextView priceView) {

        if (priceView != null)
            priceView.setText(price);
    }
    public void setPercentage(List<float[]> data, TextView percentageView) {

        if (data == null || data.size() == 0) return;

        float percentage = ( data.get(data.size() - 1)[1] - data.get(data.size()-timePeriod.getNumberOfDays())[1] )
                / data.get(data.size()-timePeriod.getNumberOfDays())[1] * 100;

        DecimalFormat df = new DecimalFormat("#.##");

        String color = percentage > 0 ? INCREASE_GREEN : DECREASE_RED;

        if (percentageView != null) {
            percentageView.setTextColor(Color.parseColor(color));
            percentageView.setText(" (" + df.format(percentage) + "%)");
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        // Call when this is not the first Tab to open, so the data is already in stockService.java.

        chart = (LineChart) view.findViewById(R.id.week_chart);
        List<float[]> data = StockService.getDailyData();

        if (chart != null && data != null && data.size() > 0) {
            setChartHelper(data, chart, getActivity());
        }

        TextView priceView = view.findViewById(R.id.stockPrice_week);
        TextView currencyView = view.findViewById(R.id.stockCurrency_week);
        Float price = StockService.getPrice();
        String currency = StockService.getCurrency();
        TextView percentageView = view.findViewById(R.id.stockPercentage_week);

        setCurrency( currency, currencyView);
        setPrice( String.valueOf(price), priceView);
        setPercentage(data, percentageView);

        return view;
    }
}