package edu.neu.cs5520.alphaobserver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockSearchResult {
    @Expose
    @SerializedName("bestMatches")
    private List<StockSearchMatch> bestMatches;

    public List<StockSearchMatch> getBestMatches() {
        return this.bestMatches;
    }
}
