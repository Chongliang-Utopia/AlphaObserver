package edu.neu.cs5520.alphaobserver.model;

public class StockCard {
    private String stockSymbol;
    private String stockType;
    private String stockPrice;
    private String stockCurrency;
    private String stockChangePercent;

    public StockCard(String stockSymbol, String stockType, String stockPrice, String stockCurrency,
                     String stockChangePercent) {
        this.stockSymbol = stockSymbol;
        this.stockType = stockType;
        this.stockPrice = stockPrice;
        this.stockCurrency = stockCurrency;
        this.stockChangePercent = stockChangePercent;
    }

    public StockCard(String stockSymbol) {
        this.stockSymbol = stockSymbol;
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

    public String getStockChangePercent() {
        return stockChangePercent;
    }

    public void setStockChangePercent(String stockChangePercent) {
        this.stockChangePercent = stockChangePercent;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockCurrency() {
        return stockCurrency;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public void setStockCurrency(String stockCurrency) {
        this.stockCurrency = stockCurrency;
    }
}
