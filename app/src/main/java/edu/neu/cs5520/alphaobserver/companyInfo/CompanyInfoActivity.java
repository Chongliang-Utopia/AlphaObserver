package edu.neu.cs5520.alphaobserver.companyInfo;

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

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.neu.cs5520.alphaobserver.R;

public class CompanyInfoActivity extends AppCompatActivity {

    RevenueFragment revenueFragment = new RevenueFragment();
    GrossCostFragment grossCostFragment = new GrossCostFragment();
    GrossProfitFragment grossProfitFragment = new GrossProfitFragment();

    Handler mainThreadHandler;

    String stockSymbol;

    static String CIK;
    static String currency;
    static String country;
    static String sector;
    static String description;
    //static String revenueTTM;
    static String year;

    TextView CIKtv;
    TextView currencytv;
    TextView countrytv;
    TextView sectortv;
    TextView descriptiontv;
    //TextView revenueTTMtv;
    TextView yeartv;

    private static String[] Results;
    private final Handler textHandler = new Handler();

    private ProgressBar progressBar;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUME", "RESUME");
        // Fetch data and create chart.
        Log.e("stockSymbol", stockSymbol);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        // TODO: Get from intent
        //Bundle data = getIntent().getExtras();
        //stockSymbol = data.getString("STOCK_SYMBOL");
        stockSymbol = "IBM";

        TextView stockSymbolText = findViewById(R.id.stockSymbol);
        stockSymbolText.setText(stockSymbol);

        //setData(stockSymbol);


        CIKtv = findViewById(R.id.response_CIK);
        currencytv = findViewById(R.id.response_Currency);
        countrytv = findViewById(R.id.response_Country);
        sectortv = findViewById(R.id.response_Sector);
        descriptiontv = findViewById(R.id.response_Description);
        //revenueTTMtv = findViewById(R.id.yearRevenue);
        yeartv = findViewById(R.id.year);
        runCallThread();

        /*sectortv.setText(sector);
        countrytv.setText(country);
        currencytv.setText(currency);
        CIKtv.setText(CIK);
        descriptiontv.setText(description);*/

        //TODO: set button
        Button refreshButton = findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(v -> refresh());
        Button showMoreBtn = findViewById(R.id.button_showMore);
        showMoreBtn.setOnClickListener(v -> openActivityDescription());

        // Fetch data and create chart.
        CompanyInfoService.setModel(revenueFragment, grossCostFragment, grossProfitFragment, mainThreadHandler, this);
        CompanyInfoService.setData(stockSymbol);

        // Fragments

        FragmentManager fm = getSupportFragmentManager();

        ViewStateAdapter sa = new ViewStateAdapter(fm, getLifecycle());
        final ViewPager2 pa = findViewById(R.id.pager);
        pa.setAdapter(sa);

        // Up to here, we have working scrollable pages

        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Revenue"));
        tabLayout.addTab(tabLayout.newTab().setText("Gross Cost"));
        tabLayout.addTab(tabLayout.newTab().setText("Gross Profit"));


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
                return revenueFragment;
            }
            else if(position == 1){
                return grossProfitFragment;
            }
            return grossCostFragment;

        }

        @Override
        public int getItemCount() {
            // Hardcoded, use lists
            return 3;
        }
    }

    void refresh() {
        // Fetch data and create chart.
        CompanyInfoService.setModel(revenueFragment, grossCostFragment, grossProfitFragment, mainThreadHandler, this);
        CompanyInfoService.setData(stockSymbol);
    }

    public void openActivityDescription() {
        Intent intent = new Intent(this, CompanyDescriptionActivity.class);
        intent.putExtra("DESCRIPTION", description);
        intent.putExtra("STOCK_SYMBOL", stockSymbol);
        startActivity(intent);
    }

    public static boolean isValidAPICall() {
        String error = Results[0];
        return error != null && !error.equals("error");
    }

    public void runCallThread(){
        runnableThread callThread = new runnableThread();
        new Thread(callThread).start();
    }

    class runnableThread implements Runnable{
        @Override
        public void run() {
            if (stockSymbol != null) {
                Results = fetchInfo();
                boolean valid = isValidAPICall();
                if(valid) {
                    textHandler.post(() -> {
                        CIKtv.setText(CIK);
                        currencytv.setText(currency);
                        countrytv.setText(country);
                        sectortv.setText(sector);
                        yeartv.setText(year.substring(0,4));
                        //revenueTTMtv.setText(revenueTTM);
                        descriptiontv.setText(description.substring(0, 100) + " ...");

                    });
                } else{

                    textHandler.post(() -> {
                        CIKtv.setText("Invalid Search");
                        currencytv.setText("Invalid Search");
                        countrytv.setText("Invalid Search");
                        sectortv.setText("Invalid Search");
                        //revenueTTMtv.setText("Invalid Search");
                        yeartv.setText("Invalid Search");
                        descriptiontv.setText("Invalid Search");

                        Toast.makeText(CompanyInfoActivity.this, "Invalid Symbol, Please Try Again", Toast.LENGTH_SHORT).show();
                    });
                }}
            else {
                textHandler.post(() -> {
                    CIKtv.setText("Invalid Search");
                    currencytv.setText("Invalid Search");
                    countrytv.setText("Invalid Search");
                    sectortv.setText("Invalid Search");
                    //revenueTTMtv.setText("Invalid Search");
                    yeartv.setText("Invalid Search");
                    descriptiontv.setText("Invalid Search");
                    Toast.makeText(CompanyInfoActivity.this, "Invalid Symbol, Please Try Again", Toast.LENGTH_SHORT).show();
                });
            }
        }


        protected String[] fetchInfo(){
            String[] results = new String[6];
            URL url;
            String URLString;
            try{
                //https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo
                URLString = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + stockSymbol + "&apikey=Q5D084P5R0KIKU10";
                //URLString = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo";


                url = new URL(URLString);
                HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
                httpconn.setRequestMethod("GET");
                httpconn.setDoInput(true);
                httpconn.connect();
                InputStream inputStream = httpconn.getInputStream();
                final String response = convertStreamToString(inputStream);
                JSONObject jObject = new JSONObject(response);
                CIK = jObject.getString("CIK");
                currency = jObject.getString("Currency");
                country = jObject.getString("Country");
                sector = jObject.getString("Sector");
                //revenueTTM = jObject.getString("RevenueTTM");
                year = jObject.getString("LatestQuarter");
                description = jObject.getString("Description");



                results[0] = CIK;
                results[1] = currency;
                results[2] = country;
                results[3] = sector;
                //results[4] = revenueTTM;
                results[4] = year;
                results[5] = description;

                //Log.e("results", results[4]);
                return results;

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            results[0] = "error";

            return results;

        }

        public String convertStreamToString(InputStream inputStream){
            StringBuilder stringBuilder=new StringBuilder();
            try {
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String len;
                while((len=bufferedReader.readLine())!=null){
                    stringBuilder.append(len);
                }
                bufferedReader.close();
                //return stringBuilder.toString().replace(",", ",\n");
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

}}