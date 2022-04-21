package edu.neu.cs5520.alphaobserver.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.adapter.StockAdaptor;
import edu.neu.cs5520.alphaobserver.adapter.StockClickListener;
import edu.neu.cs5520.alphaobserver.model.JSONPlaceholder;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockQuoteItem;
import edu.neu.cs5520.alphaobserver.model.StockQuoteResult;
import edu.neu.cs5520.alphaobserver.model.StockSave;
import edu.neu.cs5520.alphaobserver.model.StockSearchMatch;
import edu.neu.cs5520.alphaobserver.model.StockSearchResult;
import edu.neu.cs5520.alphaobserver.model.User;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDashboardActivity extends AppCompatActivity {
    private TextView greeting;
    private TextView emptyListMsg;
    private String currentUser;
    private DatabaseReference dbRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private StockAdaptor stockAdaptor;
    private List<StockCard> stockCardList;
    private Set<String> stockSet;
    private ProgressBar loadingSpinner;
    private int fetchedSearchResultSize;
    private int fetchedQuoteResultSize;
    private MaterialButton refreshButton;
    private static final String GOOD_MORNING = "Good morning, ";
    private static final String GOOD_AFTERNOON = "Good afternoon, ";
    private static final String GOOD_EVENING = "Good evening, ";
    private static final String GOOD_NIGHT = "Good night, ";
    private static final String BASE_URL = "https://www.alphavantage.co/";
    private static final String USER_NAME = "USER_NAME";
    private static final String SYMBOL_SEARCH = "SYMBOL_SEARCH";
    private static final String GLOBAL_QUOTE = "GLOBAL_QUOTE";
    private static final String API_KEY = "Q5D084P5R0KIKU10";
    private static final String FAIL_TO_FETCH_STOCK_INFO = "Cannot fetch stock information, try it later.";
    private static final String FAIL_TO_FETCH_STOCK_PRICE = "Cannot fetch stock price and change percent, try it later.";
    private static final String REFRESH_SUCCESS = "Refresh successfully!";
    private static final String REMOVE_SAVED_STOCK_SUCCESS = "Successfully remove the saved stock!";

    public UserDashboardActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_page);

        Bundle data = getIntent().getExtras();
        currentUser = data.getString(USER_NAME);

        recyclerView = (RecyclerView) findViewById(R.id.user_dashboard_recycler_view);
        loadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
        refreshButton = (MaterialButton) findViewById(R.id.button_refresh);

        dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(currentUser);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toggleResultSpinnerView(false);
                stockCardList = new ArrayList<>();
                stockSet = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StockSave stockSave = snapshot.getValue(StockSave.class);
                    String username = stockSave.getUsername();
                    String stockSymbol = stockSave.getSymbol();
                    if (username.equals(currentUser) && !stockSet.contains(stockSymbol)) {
                        stockSet.add(stockSymbol);
                        StockCard stockCard = new StockCard(stockSymbol);
                        stockCardList.add(stockCard);
                    }
                }
                if (stockCardList.isEmpty()) {
                    emptyListMsg.setVisibility(View.VISIBLE);
                } else {
                    fetchStock();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

//        add stocksave
        dbRef.push().setValue(new StockSave("linni", "IBM"));
        dbRef.push().setValue(new StockSave("linni", "TSCO.LON"));
        dbRef.push().setValue(new StockSave("linni", "SHOP.TRT"));

        greeting = (TextView) findViewById(R.id.text_dashboard_greeting);
        greeting.setText(getCurrentTime() + currentUser + "!");

        emptyListMsg = (TextView) findViewById(R.id.text_view_empty_list_msg);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchStock();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                deleteLink(viewHolder.itemView, position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteLink(View itemView, int position) {
        StockCard currentStockCard = stockCardList.get(position);
        String currentStockSymbol = currentStockCard.getStockSymbol();
        Query userQuery = dbRef.orderByChild("symbol").equalTo(currentStockSymbol);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled", error.toException());
            }
        });
        Toast.makeText(itemView.getContext(), REMOVE_SAVED_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();
    }

    private void fetchStock() {
        fetchedSearchResultSize = 0;
        fetchedQuoteResultSize = 0;
        for (StockCard stockCard : stockCardList) {
            fetchStockSearchResult(stockCard);
            fetchStockQuoteResult(stockCard);
        }
        Toast.makeText(UserDashboardActivity.this, REFRESH_SUCCESS, Toast.LENGTH_SHORT).show();
    }

    private void fetchStockQuoteResult(StockCard stockCard) {
        JSONPlaceholder jsonPlaceholder = buildJSONPlaceholder();
        String stockSymbol = stockCard.getStockSymbol();
        Call<StockQuoteResult> call = jsonPlaceholder.getStockQuoteResult(GLOBAL_QUOTE, stockSymbol, API_KEY);
        call.enqueue(new Callback<StockQuoteResult>() {
            @Override
            public void onResponse(Call<StockQuoteResult> call, Response<StockQuoteResult> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(UserDashboardActivity.this, response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                StockQuoteResult stockQuoteResult = response.body();
                StockQuoteItem quoteItem = stockQuoteResult.getQuoteItem();
                if (quoteItem == null) {
                    Toast.makeText(UserDashboardActivity.this, FAIL_TO_FETCH_STOCK_PRICE, Toast.LENGTH_SHORT).show();
                } else {
                    String stockPrice = quoteItem.getPrice();
                    String stockChangePercent = quoteItem.getChangePercent();
                    stockCard.setStockPrice(stockPrice);
                    stockCard.setStockChangePercent(stockChangePercent);
                }
                fetchedQuoteResultSize++;
                checkFetchStatus();
            }

            @Override
            public void onFailure(Call<StockQuoteResult> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, FAIL_TO_FETCH_STOCK_PRICE, Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void checkFetchStatus() {
        if (fetchedQuoteResultSize == stockCardList.size() && fetchedSearchResultSize == stockCardList.size()) {
            toggleResultSpinnerView(true);
            createRecyclerView();
        }
    }

    private void fetchStockSearchResult(StockCard stockCard) {
        JSONPlaceholder jsonPlaceholder = buildJSONPlaceholder();
        String stockSymbol = stockCard.getStockSymbol();
        Call<StockSearchResult> call = jsonPlaceholder.getStockSearchResult(SYMBOL_SEARCH, stockSymbol, API_KEY);
        call.enqueue(new Callback<StockSearchResult>() {
            @Override
            public void onResponse(Call<StockSearchResult> call, Response<StockSearchResult> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(UserDashboardActivity.this, response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                StockSearchResult stockSearchResult = response.body();
                List<StockSearchMatch> stockSearchMatchList = stockSearchResult.getBestMatches();

                if (stockSearchMatchList == null) {
                    Toast.makeText(UserDashboardActivity.this, FAIL_TO_FETCH_STOCK_INFO, Toast.LENGTH_SHORT).show();
                } else {
                    StockSearchMatch topMatch = stockSearchMatchList.get(0);
                    String stockType = topMatch.getType();
                    String stockCurrencyString = topMatch.getCurrency();
                    String stockName = topMatch.getName();
                    Currency stockCurrency = Currency.getInstance(stockCurrencyString);
                    String stockCurrencySymbol = stockCurrency.getSymbol();
                    stockCard.setStockCurrency(stockCurrencySymbol);
                    stockCard.setStockType(stockType);
                }
                fetchedSearchResultSize++;
                checkFetchStatus();
            }

            @Override
            public void onFailure(Call<StockSearchResult> call, Throwable t) {
                Toast.makeText(UserDashboardActivity.this, FAIL_TO_FETCH_STOCK_INFO, Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void createRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        stockAdaptor = new StockAdaptor(stockCardList, currentUser);
        StockClickListener stockClickListener = new StockClickListener() {
            @Override
            public void onStockClick(int position) {
                StockCard stockCard = stockCardList.get(position);
                String stockSymbol = stockCard.getStockSymbol();
                Intent intent = new Intent(UserDashboardActivity.this, StockDetailActivity.class);
                intent.putExtra("USER_NAME", currentUser);
                intent.putExtra("STOCK_SYMBOL", stockSymbol);
                startActivity(intent);
            }

            @Override
            public void onStockDelete(int position) {
                deleteLink(recyclerView, position);
            }
        };
        stockAdaptor.setOnStockClickListener(stockClickListener);
        recyclerView.setAdapter(stockAdaptor);
        recyclerView.setLayoutManager(layoutManager);
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return GOOD_MORNING;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return GOOD_AFTERNOON;
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return GOOD_EVENING;
        } else {
            return GOOD_NIGHT;
        }
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

    private void toggleResultSpinnerView(boolean showResult) {
        emptyListMsg.setVisibility(View.GONE);
        recyclerView.setVisibility(showResult ? View.VISIBLE : View.GONE);
        loadingSpinner.setVisibility(showResult ? View.GONE : View.VISIBLE);
    }
}