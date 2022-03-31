package edu.neu.cs5520.alphaobserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockQuoteResult {
    @Expose
    @SerializedName("Global Quote")
    private StockQuoteItem item;

    public StockQuoteItem getQuoteItem() {
        return this.item;
    }
}
