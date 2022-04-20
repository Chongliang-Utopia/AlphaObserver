package edu.neu.cs5520.alphaobserver.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.cs5520.alphaobserver.R;

public class ReviewHolder extends RecyclerView.ViewHolder {
    private TextView reviewUser;
    private TextView reviewTime;
    private TextView reviewContent;

    public ReviewHolder(@NonNull View itemView) {
        super(itemView);
        reviewUser = (TextView) itemView.findViewById(R.id.text_view_review_user);
        reviewTime = (TextView) itemView.findViewById(R.id.text_view_review_time);
        reviewContent = (TextView) itemView.findViewById(R.id.text_view_review_content);
    }


    public void setReviewUser(String reviewUser) {
        this.reviewUser.setText(reviewUser);
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime.setText(reviewTime);
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent.setText(reviewContent);
    }
}
