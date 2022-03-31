package edu.neu.cs5520.alphaobserver.model;

public class StockCard {
    private String stockSymbol;
    private String stockType;
    private String stockPrice;

    public StockCard(String stockSymbol, String stockType, String stockPrice) {
        this.stockSymbol = stockSymbol;
        this.stockType = stockType;
        this.stockPrice = stockPrice;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public String getStockType() {
        return stockType;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }
}
