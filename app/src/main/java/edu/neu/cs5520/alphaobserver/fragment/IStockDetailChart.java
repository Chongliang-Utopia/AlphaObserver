package edu.neu.cs5520.alphaobserver.fragment;

import android.app.Activity;
import android.widget.TextView;

import java.util.List;

public interface IStockDetailChart {
    public void setChart(List<float[]> data, Activity activity);

    public void setPrice(String price,  Activity activity);

    public void setPercentage(List<float[]> data,  Activity activity);
}
