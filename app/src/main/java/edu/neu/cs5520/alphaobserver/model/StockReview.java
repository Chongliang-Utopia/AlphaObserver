package edu.neu.cs5520.alphaobserver.model;

public class StockReview {
    private String username;
    private String symbol;
    private String review;
    private long timeStamp;

    public StockReview() {
    }

    public StockReview(String username, String symbol, String review, long timeStamp) {
        this.username = username;
        this.symbol = symbol;
        this.review = review;
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getReview() {
        return review;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
