package edu.neu.cs5520.alphaobserver.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.cs5520.alphaobserver.R;

public class StockHolder extends RecyclerView.ViewHolder {
    private TextView stockSymbol;
    private TextView stockType;
    private TextView stockPrice;

    public StockHolder(@NonNull View itemView) {
        super(itemView);
        stockSymbol = (TextView) itemView.findViewById(R.id.text_view_stock_symbol);
        stockType = (TextView) itemView.findViewById(R.id.text_view_stock_type);
        stockPrice = (TextView) itemView.findViewById(R.id.text_view_stock_price);
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol.setText(stockSymbol);
    }

    public void setStockType(String stockType) {
        this.stockType.setText(stockType);
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice.setText(stockPrice);
    }
}
