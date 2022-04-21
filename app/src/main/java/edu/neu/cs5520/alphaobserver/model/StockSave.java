package edu.neu.cs5520.alphaobserver.model;

public class StockSave {
    private String username;
    private String symbol;
    private String stockName;

    public StockSave(String username, String symbol, String stockName) {
        this.username = username;
        this.symbol = symbol;
        this.stockName = stockName;
    }

    public StockSave() {
    }

    public String getUsername() {
        return username;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStockName() { return stockName; }
}
