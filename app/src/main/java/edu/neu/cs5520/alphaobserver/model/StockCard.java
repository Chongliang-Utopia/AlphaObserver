package edu.neu.cs5520.alphaobserver.model;

public class StockCard {
    private String stockName;
    private String stockCategory;
    private String stockPrice;

    public StockCard(String stockName, String stockCategory, String stockPrice) {
        this.stockName = stockName;
        this.stockCategory = stockCategory;
        this.stockPrice = stockPrice;
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockCategory() {
        return stockCategory;
    }

    public String getStockPrice() {
        return stockPrice;
    }
}
