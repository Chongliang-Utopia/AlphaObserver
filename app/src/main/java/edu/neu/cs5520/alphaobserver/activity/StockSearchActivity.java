package edu.neu.cs5520.alphaobserver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.adapter.StockAdaptor;
import edu.neu.cs5520.alphaobserver.model.JSONPlaceholder;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockSearchActivity extends AppCompatActivity {
    static final String BASE_URL = "https://www.alphavantage.co/";
    static final String API_KEY = "42Z7B1OTYEEJQCOT";
    static final String FAIL_TO_FETCH_STOCK_INFO = "Cannot fetch stock information, try it later.";
    static final String FAIL_TO_FETCH_STOCK_PRICE = "Cannot fetch stock price and change percent, try it later.";

    List<StockCard> stockCardList = new ArrayList<>();
    RecyclerView recyclerView;
    StockAdaptor stockAdaptor;
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

            @Override
            public void onClick(View view) {

            }
        });
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        stockAdaptor = new StockAdaptor(stockCardList, currentUser);
        recyclerView.setAdapter(stockAdaptor);
        recyclerView.setLayoutManager(rLayoutManger);
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