package edu.neu.cs5520.alphaobserver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.StockCard;

public class StockAdaptor extends RecyclerView.Adapter<StockHolder> {
    private final List<StockCard> stockCardList;
    private String currentUser;

    public StockAdaptor(List<StockCard> stockCardList, String currentUser) {
        this.stockCardList = stockCardList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_card, parent, false);
        return new StockHolder(view, currentUser);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        StockCard stockCard = stockCardList.get(position);
        String stockPrice = stockCard.getStockPrice();
        holder.setStockSymbol(stockCard.getStockSymbol());
        holder.setStockType(stockCard.getStockType());
        holder.setStockPrice(stockPrice);
        holder.setStockChangePercent(stockCard.getStockChangePercent());
        holder.setStockCurrency(stockPrice, stockCard.getStockCurrency());
    }

    @Override
    public int getItemCount() {
        return stockCardList.size();
    }
}
