package edu.neu.cs5520.alphaobserver.adapter;

public interface StockClickListener {
    void onStockClick(int position);
    void onStockDelete(int position);
}
