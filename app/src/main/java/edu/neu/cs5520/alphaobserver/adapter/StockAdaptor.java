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

    public StockAdaptor(List<StockCard> stockCardList) {
        this.stockCardList = stockCardList;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_card, parent, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        StockCard stockCard = stockCardList.get(position);
        holder.setStockSymbol(stockCard.getStockSymbol());
        holder.setStockType(stockCard.getStockType());
        holder.setStockPrice(stockCard.getStockPrice());
    }

    @Override
    public int getItemCount() {
        return stockCardList.size();
    }
}
