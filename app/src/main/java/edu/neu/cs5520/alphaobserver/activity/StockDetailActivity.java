package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.fragment.MonthFragment;
import edu.neu.cs5520.alphaobserver.service.StockService;
import edu.neu.cs5520.alphaobserver.fragment.WeekFragment;

public class StockDetailActivity extends AppCompatActivity {

    WeekFragment weekFragment = new WeekFragment();
    MonthFragment monthFragment = new MonthFragment();
    Handler mainThreadHandler;

    String stockSymbol;
    String stockName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        // TODO: Get from intent
        stockSymbol = "PINS";
        stockName = "Pinterest";

        // TODO: verify
        StockService.setModel(weekFragment, monthFragment, mainThreadHandler, this);
        // TODO: get stock name
        StockService.setData(stockSymbol);

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
}