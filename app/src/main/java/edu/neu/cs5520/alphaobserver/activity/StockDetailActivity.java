package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.fragment.MonthFragment;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockReview;
import edu.neu.cs5520.alphaobserver.model.StockSave;
import edu.neu.cs5520.alphaobserver.service.StockService;
import edu.neu.cs5520.alphaobserver.fragment.WeekFragment;

public class StockDetailActivity extends AppCompatActivity {

    WeekFragment weekFragment = new WeekFragment();
    MonthFragment monthFragment = new MonthFragment();
    Handler mainThreadHandler;

    String stockSymbol;
    String stockName;
    String currentUser;

    int stockSaveCnt = 0;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        // TODO: Get from intent
        Bundle data = getIntent().getExtras();
        currentUser = data.getString("USER_NAME");
        stockSymbol = data.getString("STOCK_SYMBOL");
        stockName = data.getString("STOCK_NAME");

        TextView stockNameText = findViewById(R.id.stockName);
        stockNameText.setText(stockName);
        TextView stockSymbolText = findViewById(R.id.stockNSymbol);
        stockSymbolText.setText(stockSymbol);


        StockService.setModel(weekFragment, monthFragment, mainThreadHandler, this);
        StockService.setData(stockSymbol);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Review").child(stockSymbol).push();
        // Task t = myRef.setValue(new StockReview(currentUser, stockSymbol, "This stock is good! \nThis stock is good!", System.currentTimeMillis()));
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("StockSave");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap<String, String>> stockSaveMap = (HashMap<String, HashMap<String, String>>)snapshot.getValue();
                    for (String key: stockSaveMap.keySet()) {
                        HashMap<String, String> map = stockSaveMap.get(key);
                        String symbol = map.get("symbol");
                        if (symbol.equals(stockSymbol)) {
                            stockSaveCnt++;
                        }
                    }
                }
                TextView stockNameText = findViewById(R.id.saveNumber);
                stockNameText.setText(String.valueOf(stockSaveCnt));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // Fragments

        FragmentManager fm = getSupportFragmentManager();

        ViewStateAdapter sa = new ViewStateAdapter(fm, getLifecycle());
        final ViewPager2 pa = findViewById(R.id.pager);
        pa.setAdapter(sa);

        // Up to here, we have working scrollable pages

        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Week"));
        tabLayout.addTab(tabLayout.newTab().setText("Month"));

        // Now we have tabs, NOTE: I am hardcoding the order, you'll want to do something smarter

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pa.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        pa.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // And now we have tabs that, when clicked, navigate to the correct page
    }
    public void setStockPrice (float price) {
        TextView textView = (TextView) findViewById(R.id.stockPrice);
        textView.setText("$" + String.valueOf(price));
    }
    private class ViewStateAdapter extends FragmentStateAdapter {

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Hardcoded in this order, you'll want to use lists and make sure the titles match
            if (position == 0) {
                return weekFragment;
            }
            return monthFragment;
        }

        @Override
        public int getItemCount() {
            // Hardcoded, use lists
            return 2;
        }
    }
    public void buttonOnClick(View v) {
        switch (v.getId()) {
            case R.id.StockReview:
                Intent intent= new Intent(StockDetailActivity.this, ReviewActivity.class);
                intent.putExtra("STOCK_SYMBOL", stockSymbol);
                intent.putExtra("STOCK_NAME", stockName);
                intent.putExtra("USER_NAME", currentUser);
                startActivity(intent);
                break;
            case R.id.AboutCompany:
                break;
            case R.id.button_stock_save:
                Log.e("button", "clicked");
                break;
            default:
                break;
        }
    }
}