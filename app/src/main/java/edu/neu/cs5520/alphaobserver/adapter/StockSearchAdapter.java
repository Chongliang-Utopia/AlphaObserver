package edu.neu.cs5520.alphaobserver.adapter;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.activity.EntryActivity;
import edu.neu.cs5520.alphaobserver.activity.UserDashboardActivity;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockSave;
import edu.neu.cs5520.alphaobserver.activity.StockDetailActivity;

public class StockSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<StockCard> stockCardList;
    static final String API_KEY = "Q5D084P5R0KIKU10";
//    ItemClickListener listener;
    String currentUser;
    private static Handler mainThreadHandler;

    public StockSearchAdapter(List<StockCard> stockCardList) {
        this.stockCardList = stockCardList;
    }

    public StockSearchAdapter(List<StockCard> stockCardList, String currentUser, Handler mainThreadHandler) {
        this.stockCardList = stockCardList;
        this.currentUser = currentUser;
        this.mainThreadHandler = mainThreadHandler;

    }

//    interface ItemClickListener {
//        void onItemClick(String userName, String symbol);
//    }

    static class StockSearchHolder extends RecyclerView.ViewHolder {
        private String currentUser;
        private DatabaseReference dbRef;
        private TextView stockSymbol;
        private TextView stockType;
        private TextView stockCurrency;
        private TextView stockPrice;
        private TextView stockChangePercent;
        private MaterialButton saveButton;
        private LinearLayout stockInfoLayout;
//        private static Handler handler;
        private static final String INCREASE_GREEN = "#FF4CAF50";
        private static final String DECREASE_RED = "#FFF44336";
        private static final String NO_CHANGE_BLACK = "#363B46";
        private static final String NA = "N/A";
        private static final String REMOVE_SAVED_STOCK_SUCCESS = "Successfully remove the saved stock!";
        private static final String EMPTY_CHART_OR_COMPANY_INFO = "The API doesn't have record for this stock, please try other stocks!";
        static final String SAVE_STOCK_SUCCESS = "Successfully save the stock!";

        public StockSearchHolder(@NonNull View itemView) {
            super(itemView);
        }
        public StockSearchHolder(@NonNull View itemView, String currentUser) {
            super(itemView);
            this.currentUser = currentUser;
            dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(this.currentUser);
            stockSymbol = (TextView) itemView.findViewById(R.id.stock_search_text_view_stock_symbol);
            stockType = (TextView) itemView.findViewById(R.id.stock_search_text_view_stock_type);
            stockPrice = (TextView) itemView.findViewById(R.id.stock_search_text_view_stock_price);
            stockChangePercent = (TextView) itemView.findViewById(R.id.stock_search_text_view_stock_change_percent);
            stockCurrency = (TextView) itemView.findViewById(R.id.stock_search_text_view_stock_currency);
            saveButton = (MaterialButton) itemView.findViewById(R.id.stock_search_button_stock_save);
            stockInfoLayout = (LinearLayout) itemView.findViewById(R.id.stockSearchInfoLayout);
            stockInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String symbol = stockSymbol.getText().toString();
                            JSONObject dataResponseForIntraDay = fetchStockData(getIntraDayURL(symbol));
                            JSONObject dataResponseForCompany = fetchStockData(getCompanyURL(symbol));
                            JSONObject dataResponseForCompanyInfo = fetchStockData(getCompanyInfoURL(symbol));
                            if (!checkIfIntraDayDataIsEmpty(dataResponseForIntraDay) &&
                                !checkIfCompanyDataIsEmpty(dataResponseForCompany) &&
                                !checkIfCompanyInfoDataIsEmpty(dataResponseForCompanyInfo)) {
                                Intent i = new Intent(view.getContext(), StockDetailActivity.class);
                                i.putExtra("STOCK_SYMBOL", stockSymbol.getText());
                                i.putExtra("STOCK_NAME", stockSymbol.getText());
                                i.putExtra("USER_NAME", currentUser);
                                view.getContext().startActivity(i);
                            } else {
                                try {
                                    mainThreadHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(itemView.getContext(), EMPTY_CHART_OR_COMPANY_INFO, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();




                }
            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String currentStockSymbol = stockSymbol.getText().toString();
                    Query userQuery = dbRef.orderByChild("symbol").equalTo(currentStockSymbol);
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
//                                System.out.println(snapshot.getRef().toString());
                                for (DataSnapshot ds :snapshot.getChildren()) ds.getRef().removeValue();
                                Toast.makeText(view.getContext(), REMOVE_SAVED_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();
                                saveButton.setIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_border_24));

                            } else {
                                DatabaseReference newPostRef = dbRef.push();
                                try {
                                    newPostRef.setValue(new StockSave(currentUser, currentStockSymbol), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            if (error == null) {
                                                Toast.makeText(view.getContext(), SAVE_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();
                                                saveButton.setIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_24));
                                            }

                                        }
                                    });
                                } catch (Exception e) {
                                    Log.v(TAG, e.toString());
                                }


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

        private void setValidString(TextView view, String s) {
            if (s == null) {
                view.setText(NA);
            } else {
                view.setText(s);
            }
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


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_search_card, parent, false);
        return new StockSearchHolder(view, currentUser);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StockCard stockCard = stockCardList.get(position);
        ((StockSearchHolder) holder).setStockSymbol(stockCard.getStockSymbol());
        ((StockSearchHolder) holder).setStockType(stockCard.getStockType());
        ((StockSearchHolder) holder).setStockPrice(stockCard.getStockPrice());
        ((StockSearchHolder) holder).setStockCurrency(stockCard.getStockPrice(), stockCard.getStockCurrency());
        ((StockSearchHolder) holder).setStockChangePercent(stockCard.getStockChangePercent());

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(this.currentUser);
        String stockSymbol = stockCard.getStockSymbol();
        Query userQuery = dbRef.orderByChild("symbol").equalTo(stockSymbol);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) ((StockSearchHolder) holder).saveButton.setIcon(ContextCompat.getDrawable(((StockSearchHolder) holder).saveButton.getContext(), R.drawable.ic_baseline_star_24));
                else ((StockSearchHolder) holder).saveButton.setIcon(ContextCompat.getDrawable(((StockSearchHolder) holder).saveButton.getContext(), R.drawable.ic_baseline_star_border_24));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

//    public void setItemClickListener(ItemClickListener listener) {
//        this.listener = listener;
//    }

    @Override
    public int getItemCount() {
        return stockCardList.size();
    }

    static String getIntraDayURL(String stockSymbol) {
        return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stockSymbol + "&interval=30min" +
                "&apikey=" + API_KEY;
    }

    static String getCompanyURL(String stockSymbol) {
        return "https://www.alphavantage.co/query?function=INCOME_STATEMENT&symbol=" + stockSymbol +
                "&apikey=" + API_KEY;
    }

    static String getCompanyInfoURL(String stockSymbol) {
        return "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + stockSymbol +
                "&apikey=" + API_KEY;
    }

    static boolean checkIfIntraDayDataIsEmpty(JSONObject response) {
        try {
            response.getJSONObject("Meta Data");
            return false;
        } catch (JSONException e) {
            return true;
        }
    }

    static boolean checkIfCompanyDataIsEmpty(JSONObject response) {
        try {
            response.getJSONArray("annualReports");
            return false;
        } catch (JSONException e) {
            return true;
        }
    }

    static boolean checkIfCompanyInfoDataIsEmpty(JSONObject response) {
        try {
            response.getString("AssetType");
            return false;
        } catch (JSONException e) {
            return true;
        }
    }

    public static JSONObject fetchStockData(String URL_WEB) {


        URL url = null;
        try {

            url = new URL(URL_WEB);
            //url = new URL(params[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            // Read response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            JSONObject jObject = new JSONObject(resp);

            Log.e(TAG,jObject.toString());

            return jObject;

        } catch (MalformedURLException e) {
            Log.e(TAG,"MalformedURLException");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e(TAG,"ProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG,"IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG,"JSONException");
            e.printStackTrace();
        }

        return new JSONObject();
    }

    /**
     * Helper function
     * @param is
     * @return
     */
    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
