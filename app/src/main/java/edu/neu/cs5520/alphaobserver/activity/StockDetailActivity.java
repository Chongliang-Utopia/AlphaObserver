package edu.neu.cs5520.alphaobserver.activity;

import static android.content.ContentValues.TAG;

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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.fragment.DayFragment;
import edu.neu.cs5520.alphaobserver.fragment.MonthFragment;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockReview;
import edu.neu.cs5520.alphaobserver.model.StockSave;
import edu.neu.cs5520.alphaobserver.service.StockService;
import edu.neu.cs5520.alphaobserver.fragment.WeekFragment;

public class StockDetailActivity extends AppCompatActivity {

    WeekFragment weekFragment = new WeekFragment();
    MonthFragment monthFragment = new MonthFragment();
    DayFragment dayFragment = new DayFragment();
    Handler mainThreadHandler;

    String stockSymbol;
    String stockName;
    String currentUser;

    int stockSaveCnt = 0;

    Button savedButton;
    Button unsavedButton;

    boolean saved = false;

    private DatabaseReference mDatabase;

    private ProgressBar progressBar;

    private static final String REMOVE_SAVED_STOCK_SUCCESS = "Successfully remove the saved stock!";
    private static final String ADD_SAVED_STOCK_SUCCESS = "Successfully saved stock!";

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//
//    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUME", "RESUME");
        // Fetch data and create chart.
        Log.e("stockSymbol", stockSymbol);
//        StockService.setModel(weekFragment, monthFragment, mainThreadHandler, this);
//        StockService.setData(stockSymbol);
    }

    void refresh() {
        // Fetch data and create chart.
        StockService.setModel(weekFragment, monthFragment, dayFragment, mainThreadHandler, this);
        StockService.setData(stockSymbol);
    }

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

        Button aboutCompanyBtn = findViewById(R.id.AboutCompany);
        aboutCompanyBtn.setText("About " + stockName);

        savedButton = findViewById(R.id.button_stock_save);
        unsavedButton = findViewById(R.id.button_stock_unsave);


        // Fetch data and create chart.
        StockService.setModel(weekFragment, monthFragment, dayFragment, mainThreadHandler, this);
        StockService.setData(stockSymbol);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        // DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Review").child(stockSymbol).push();
        // Task t = myRef.setValue(new StockReview(currentUser, stockSymbol, "This stock is good! \nThis stock is good!", System.currentTimeMillis()));
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("StockSave");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stockSaveCnt = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, HashMap<String, String>> stockSaveMap = (HashMap<String, HashMap<String, String>>)snapshot.getValue();
                    for (String key: stockSaveMap.keySet()) {
                        HashMap<String, String> map = stockSaveMap.get(key);
                        String symbol = map.get("symbol");
                        if (!symbol.equals(stockSymbol)) {
                            continue;
                        }
                        stockSaveCnt++;
                        String username = map.get("username");
                        if (username.equals(currentUser)) {
                            saved = true;
                            savedButton.setVisibility(View.VISIBLE);
                            unsavedButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                TextView stockNameText = findViewById(R.id.saveNumber);
                stockNameText.setText(String.valueOf(stockSaveCnt));

                if (!saved) {
                    savedButton.setVisibility(View.INVISIBLE);
                    unsavedButton.setVisibility(View.VISIBLE);
                }
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
        tabLayout.addTab(tabLayout.newTab().setText("1 Day"));
        tabLayout.addTab(tabLayout.newTab().setText("1 Week"));
        tabLayout.addTab(tabLayout.newTab().setText("1 Month"));

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
    public void setProgressBarInvisible() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
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
                return dayFragment;
            }
            else if (position == 1) {
                return weekFragment;
            }
            return monthFragment;
        }

        @Override
        public int getItemCount() {
            // Hardcoded, use lists
            return 3;
        }
    }

    private void unSaveStock(View view) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(this.currentUser);
        Query userQuery = dbRef.orderByChild("symbol").equalTo(stockSymbol);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    Toast.makeText(view.getContext(), REMOVE_SAVED_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();
                    savedButton.setVisibility(View.INVISIBLE);
                    unsavedButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void saveStock(View view) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave").child(this.currentUser).push();
        Task t = dbRef.setValue(new StockSave(currentUser, stockSymbol));

        if (t.isSuccessful()) {
            Toast.makeText(view.getContext(), ADD_SAVED_STOCK_SUCCESS, Toast.LENGTH_SHORT).show();

            savedButton.setVisibility(View.VISIBLE);
            unsavedButton.setVisibility(View.INVISIBLE);
        }
    }
    public void buttonOnClick(View v) {
        switch (v.getId()) {
            case R.id.StockReview:
                Intent intent = new Intent(StockDetailActivity.this, ReviewActivity.class);
                intent.putExtra("STOCK_SYMBOL", stockSymbol);
                intent.putExtra("STOCK_NAME", stockName);
                intent.putExtra("USER_NAME", currentUser);
                startActivity(intent);
                break;
            case R.id.AboutCompany:
                Intent i = new Intent(StockDetailActivity.this, CompanyInfoActivity.class);
                i.putExtra("STOCK_SYMBOL", stockSymbol);
                i.putExtra("STOCK_NAME", stockName);
                i.putExtra("USER_NAME", currentUser);
                startActivity(i);
                break;
            case R.id.button_stock_unsave:
                saveStock(v);
                break;
            case R.id.button_stock_save:
                unSaveStock(v);
                break;
            case R.id.button_refresh:
                refresh();
                break;
            default:
                break;
        }
    }
}