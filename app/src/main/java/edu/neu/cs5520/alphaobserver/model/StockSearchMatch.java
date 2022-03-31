package edu.neu.cs5520.alphaobserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockSearchMatch {
    @Expose
    @SerializedName("1. symbol")
    private String symbol;

    @Expose
    @SerializedName("2. name")
    private String name;

    @Expose
    @SerializedName("3. type")
    private String type;

    @Expose
    @SerializedName("4. region")
    private String region;

    @Expose
    @SerializedName("5. marketOpen")
    private String marketOpen;

    @Expose
    @SerializedName("6. marketClose")
    private String marketClose;

    @Expose
    @SerializedName("7. timezone")
    private String timezone;

    @Expose
    @SerializedName("8. currency")
    private String currency;

    @Expose
    @SerializedName("9. matchScore")
    private String matchScore;

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRegion() {
        return region;
    }

    public String getMarketOpen() {
        return marketOpen;
    }

    public String getMarketClose() {
        return marketClose;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMatchScore() {
        return matchScore;
    }
}
