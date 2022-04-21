package edu.neu.cs5520.alphaobserver.model;

public class StockSave {
    private String username;
    private String symbol;

    public StockSave(String username, String symbol) {
        this.username = username;
        this.symbol = symbol;
    }

    public StockSave() {
    }

    public String getUsername() {
        return username;
    }

    public String getSymbol() {
        return symbol;
    }
}
