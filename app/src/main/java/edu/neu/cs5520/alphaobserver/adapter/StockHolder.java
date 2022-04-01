package edu.neu.cs5520.alphaobserver.adapter;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.activity.UserDashboardActivity;
import edu.neu.cs5520.alphaobserver.model.StockSave;

public class StockHolder extends RecyclerView.ViewHolder {
    private String currentUser;
    private DatabaseReference dbRef;
    private TextView stockSymbol;
    private TextView stockType;
    private TextView stockCurrency;
    private TextView stockPrice;
    private TextView stockChangePercent;
    private MaterialButton saveButton;
    private static final String INCREASE_GREEN = "#FF4CAF50";
    private static final String DECREASE_RED = "#FFF44336";
    private static final String NO_CHANGE_BLACK = "#363B46";
    private static final String NA = "N/A";
    private static final String REMOVE_SAVED_STOCK_SUCCESS = "Successfully remove the saved stock!";

    public StockHolder(@NonNull View itemView, String currentUser) {
        super(itemView);

        this.currentUser = currentUser;
        dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(this.currentUser);

        stockSymbol = (TextView) itemView.findViewById(R.id.text_view_stock_symbol);
        stockType = (TextView) itemView.findViewById(R.id.text_view_stock_type);
        stockPrice = (TextView) itemView.findViewById(R.id.text_view_stock_price);
        stockChangePercent = (TextView) itemView.findViewById(R.id.text_view_stock_change_percent);
        stockCurrency = (TextView) itemView.findViewById(R.id.text_view_stock_currency);
        saveButton = (MaterialButton) itemView.findViewById(R.id.button_stock_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentStockSymbol = stockSymbol.getText().toString();
                Query userQuery = dbRef.orderByChild("symbol").equalTo(currentStockSymbol);
                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                            Toast.makeText(view.getContext(), REMOVE_SAVED_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled", error.toException());
                    }
                });

            }
        });
    }

    public void setStockSymbol(String stockSymbol) {
        setValidString(this.stockSymbol, stockSymbol);
    }

    public void setStockType(String stockType) {
        setValidString(this.stockType, stockType);
    }

    public void setStockPrice(String stockPrice) {
        setValidString(this.stockPrice, stockPrice);
    }

    public void setStockCurrency(String stockPrice, String stockCurrency) {
        if (stockPrice != null) {
            this.stockCurrency.setText(stockCurrency);
        } else {
            this.stockCurrency.setText("");
        }
    }

    private void setValidString(TextView view, String s) {
        if (s == null) {
            view.setText(NA);
        } else {
            view.setText(s);
        }
    }

    public void setStockChangePercent(String stockChangePercent) {
        setValidString(this.stockChangePercent, stockChangePercent);
        if (stockChangePercent != null) {
            String color = NO_CHANGE_BLACK;
            double parsedChangePercent = Double.parseDouble(stockChangePercent.replace("%", ""));
            if (stockChangePercent.startsWith("-")) {
                color = DECREASE_RED;
            } else if (parsedChangePercent > 0) {
                color = INCREASE_GREEN;
            }
            this.stockChangePercent.setTextColor(Color.parseColor(color));
        }
    }
}
