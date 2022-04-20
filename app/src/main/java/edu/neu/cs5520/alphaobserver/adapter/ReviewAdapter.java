package edu.neu.cs5520.alphaobserver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockReview;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder>{
    private final List<StockReview> reviewCardList;

    public ReviewAdapter(List<StockReview> reviewCardList) {
        this.reviewCardList = reviewCardList;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_review_card, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        StockReview reviewCard = reviewCardList.get(position);
        holder.setReviewUser(reviewCard.getUsername());
        holder.setReviewTime( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(reviewCard.getTimeStamp()));
        holder.setReviewContent(reviewCard.getReview());
    }

    @Override
    public int getItemCount() {
        return reviewCardList.size();
    }
}
