package edu.neu.cs5520.alphaobserver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.adapter.StockAdaptor;
import edu.neu.cs5520.alphaobserver.adapter.StockSearchAdapter;
import edu.neu.cs5520.alphaobserver.model.JSONPlaceholder;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockQuoteItem;
import edu.neu.cs5520.alphaobserver.model.StockQuoteResult;
import edu.neu.cs5520.alphaobserver.model.StockSearchMatch;
import edu.neu.cs5520.alphaobserver.model.StockSearchResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockSearchActivity extends AppCompatActivity {
    static final String BASE_URL = "https://www.alphavantage.co/";
    static final String API_KEY = "Q5D084P5R0KIKU10";
    static final String SYMBOL_SEARCH = "SYMBOL_SEARCH";
    static final String GLOBAL_QUOTE = "GLOBAL_QUOTE";
    static final String FAIL_TO_FETCH_STOCK_INFO = "Cannot fetch stock information, try it later.";
    static final String FAIL_TO_FETCH_STOCK_PRICE = "Cannot fetch stock price and change percent, try it later.";

    List<StockCard> stockCardList = new ArrayList<>();
    RecyclerView recyclerView;
    StockSearchAdapter stockAdaptor;
    RecyclerView.LayoutManager rLayoutManger;
    EditText searchInput;
    Button searchBtn;
    String currentUser;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("USER_NAME");
        dbRef = FirebaseDatabase.getInstance().getReference().child("User");
        setContentView(R.layout.activity_stock_search);
        searchInput = (EditText) findViewById(R.id.stockSeachInput);
        searchBtn = (Button) findViewById(R.id.stockSearchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                if (searchInput.getText().toString().length() > 0) {
                    stockCardList.clear();
                    fetchStockSearchResult(searchInput.getText().toString());
                } else Toast.makeText(StockSearchActivity.this, "Symbol cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        createRecyclerView();
    }

    private void fetchStockQuoteResult(String stockSymbol, String stockType, String stockCurrency) {
        JSONPlaceholder jsonPlaceholder = buildJSONPlaceholder();
        Call<StockQuoteResult> call = jsonPlaceholder.getStockQuoteResult(GLOBAL_QUOTE, stockSymbol, API_KEY);
        call.enqueue(new Callback<StockQuoteResult>() {
            @Override
            public void onResponse(Call<StockQuoteResult> call, Response<StockQuoteResult> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(StockSearchActivity.this, response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                StockQuoteResult stockQuoteResult = response.body();
                StockQuoteItem quoteItem = stockQuoteResult.getQuoteItem();
                if (!stockSymbol.contains(".")) {
                    if (quoteItem == null) {
                        stockCardList.add(new StockCard(stockSymbol, stockType, null, stockCurrency, null));
                    } else {
                        String stockPrice = quoteItem.getPrice();
                        String stockChangePercent = quoteItem.getChangePercent();
                        stockCardList.add(new StockCard(stockSymbol, stockType, stockPrice, stockCurrency, stockChangePercent));
                    }

                }
                stockAdaptor.notifyDataSetChanged();
                System.out.printf("Stock size: %d\n", stockCardList.size());
            }

            @Override
            public void onFailure(Call<StockQuoteResult> call, Throwable t) {
                Toast.makeText(StockSearchActivity.this, FAIL_TO_FETCH_STOCK_PRICE, Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
            }
        });
    }


    private void fetchStockSearchResult(String stockSymbol) {
        JSONPlaceholder jsonPlaceholder = buildJSONPlaceholder();
        Call<StockSearchResult> call = jsonPlaceholder.getStockSearchResult(SYMBOL_SEARCH, stockSymbol, API_KEY);
        call.enqueue(new Callback<StockSearchResult>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<StockSearchResult> call, Response<StockSearchResult> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(StockSearchActivity.this, response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                StockSearchResult stockSearchResult = response.body();
                List<StockSearchMatch> stockSearchMatchList = stockSearchResult.getBestMatches();

                if (stockSearchMatchList == null) {
                    Toast.makeText(StockSearchActivity.this, FAIL_TO_FETCH_STOCK_INFO, Toast.LENGTH_SHORT).show();
                } else {
                    for (StockSearchMatch ssm : stockSearchMatchList) {
                        String stockType = ssm.getType();
                        String stockCurrencyString = ssm.getCurrency();
                        Currency stockCurrency = Currency.getInstance(stockCurrencyString);
                        String stockCurrencySymbol = stockCurrency.getSymbol();
//                        System.out.println(ssm.getSymbol());
                        fetchStockQuoteResult(ssm.getSymbol(), stockType, stockCurrencySymbol);
                    }
                }
            }

            @Override
            public void onFailure(Call<StockSearchResult> call, Throwable t) {
                Toast.makeText(StockSearchActivity.this, FAIL_TO_FETCH_STOCK_INFO, Toast.LENGTH_SHORT).show();
//                loadingSpinner.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.stockSearchResultRecyclerView);
        stockAdaptor = new StockSearchAdapter(stockCardList, currentUser);
        recyclerView.setAdapter(stockAdaptor);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setHasFixedSize(true);
    }

    private JSONPlaceholder buildJSONPlaceholder() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(JSONPlaceholder.class);
    }
}