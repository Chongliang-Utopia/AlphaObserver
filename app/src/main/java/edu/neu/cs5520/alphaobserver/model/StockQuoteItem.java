package edu.neu.cs5520.alphaobserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockQuoteItem {
    @Expose
    @SerializedName("1. symbol")
    private String symbol;

    @Expose
    @SerializedName("02. open")
    private String open;

    @Expose
    @SerializedName("03. high")
    private String high;

    @Expose
    @SerializedName("04. low")
    private String low;

    @Expose
    @SerializedName("05. price")
    private String price;

    @Expose
    @SerializedName("06. volume")
    private String volume;

    @Expose
    @SerializedName("07. latest trading day")
    private String lastTradingDay;

    @Expose
    @SerializedName("08. previous close")
    private String previousClose;

    @Expose
    @SerializedName("09. change")
    private String change;

    @Expose
    @SerializedName("10. change percent")
    private String changePercent;

    public String getSymbol() {
        return symbol;
    }

    public String getOpen() {
        return open;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getPrice() {
        return price;
    }

    public String getVolume() {
        return volume;
    }

    public String getLastTradingDay() {
        return lastTradingDay;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public String getChange() {
        return change;
    }

    public String getChangePercent() {
        return changePercent;
    }
}
