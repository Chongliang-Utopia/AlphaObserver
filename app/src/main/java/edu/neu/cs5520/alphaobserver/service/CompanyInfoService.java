package edu.neu.cs5520.alphaobserver.service;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.util.LogPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.activity.CompanyInfoActivity;
import edu.neu.cs5520.alphaobserver.fragment.GrossCostFragment;
import edu.neu.cs5520.alphaobserver.fragment.GrossProfitFragment;
import edu.neu.cs5520.alphaobserver.fragment.RevenueFragment;
import edu.neu.cs5520.alphaobserver.model.JSONPlaceholder;
import edu.neu.cs5520.alphaobserver.model.StockSearchMatch;
import edu.neu.cs5520.alphaobserver.model.StockSearchResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyInfoService {
    private static final String INCOME_STATEMENT = "INCOME_STATEMENT";
    private static final String API_KEY = "3S6S2S5CY1C8NEE6";
    private static final String BASE_URL = "https://www.alphavantage.co/";

    static List<float[]> revenueData;
    static List<float[]> profitData;
    static List<float[]> costData;
    private static final String TAG = "WebServiceActivity";

    private static RevenueFragment revenueFragment;
    private static GrossCostFragment grossCostFragment;
    private static GrossProfitFragment grossProfitFragment;
    private static Handler handler;
    private static CompanyInfoActivity activity;


    public static void setModel(RevenueFragment revenueFrag, GrossCostFragment grossCostFrag,
                                GrossProfitFragment grossProfitFrag, Handler mainThreadHandler, CompanyInfoActivity companyInfoActivity) {
        revenueFragment = revenueFrag;
        grossCostFragment = grossCostFrag;
        grossProfitFragment = grossProfitFrag;
        handler = mainThreadHandler;
        activity = companyInfoActivity;
    }

    public static List<float[]> getRevenueData() {
        return revenueData;
    }
    public static List<float[]> getProfitData() {
        return profitData;
    }
    public static List<float[]> getCostData() {
        return costData;
    }


    public static void setData(final String stockSymbol)
    {
        //fetchStockSearchResult(stockSymbol);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject response = fetchStockData(getURl(stockSymbol));
                revenueData = parseRevenueData(response);
                profitData = parseProfitData(response);
                costData = parseCostData(response);

                //Log.e(TAG, String.valueOf(data));

                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (revenueData.isEmpty() || profitData.isEmpty() || costData.isEmpty()) {

                            } else {

                                revenueFragment.setChart(revenueData, activity);

                                grossProfitFragment.setChart(profitData, activity);

                                grossCostFragment.setChart(costData, activity);

                                activity.setProgressBarInvisible();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static List<float[]> parseRevenueData(JSONObject response) {

        List<float[]> data = new ArrayList<>();

        try {
            JSONArray annualReports = response.getJSONArray("annualReports");
            for (int i = 0; i < annualReports.length(); i++) {

                JSONObject obj = annualReports.getJSONObject(i);
                String time = obj.getString("fiscalDateEnding");
                //annualReports.get(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date)formatter.parse(time);
                assert date != null;
                long mills = date.getTime();

                String year = time.substring(0,4);
                int Y = Integer.parseInt(year);

                long revenue = obj.getLong("totalRevenue");

                //String testRevenue = obj.getString("totalRevenue");
                //Log.e("revenue", testRevenue);

                data.add(new float[] {Y, revenue});
            }
            //Collections.reverse(data);


        } catch (JSONException | ParseException e) {
            Log.e("JSONException", String.valueOf(e));
            e.printStackTrace();
        }
        return data;
    }

    static List<float[]> parseProfitData(JSONObject response) {

        List<float[]> data = new ArrayList<>();

        try {
            JSONArray annualReports = response.getJSONArray("annualReports");
            for (int i = 0; i < annualReports.length(); i++) {

                JSONObject obj = annualReports.getJSONObject(i);
                String time = obj.getString("fiscalDateEnding");
                //annualReports.get(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date)formatter.parse(time);
                long mills = date.getTime();

                String year = time.substring(0,4);
                int Y = Integer.parseInt(year);

                long profit = obj.getLong("grossProfit");


                data.add(new float[] {Y, profit});
            }
            //Collections.reverse(data);

        } catch (JSONException | ParseException e) {
            Log.e("JSONException", String.valueOf(e));
            e.printStackTrace();
        }
        return data;
    }

    static List<float[]> parseCostData(JSONObject response) {

        List<float[]> data = new ArrayList<>();

        try {
            JSONArray annualReports = response.getJSONArray("annualReports");
            for (int i = 0; i < annualReports.length(); i++) {

                JSONObject obj = annualReports.getJSONObject(i);
                String time = obj.getString("fiscalDateEnding");
                //annualReports.get(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date)formatter.parse(time);
                long mills = date.getTime();


                String year = time.substring(0,4);
                int Y = Integer.parseInt(year);
                //int cost = obj.getInt("costOfRevenue");
                long cost = obj.getLong("costOfRevenue");
                //String costTest = obj.getString("costOfRevenue");
                //Log.e("COST", costTest);

                data.add(new float[] {Y, cost});
            }
            //Collections.reverse(data);

        } catch (JSONException | ParseException e) {
            Log.e("JSONException", String.valueOf(e));
            e.printStackTrace();
        }
        return data;
    }

    static String getURl(String stockSymbol) {
        return "https://www.alphavantage.co/query?function=INCOME_STATEMENT&symbol=" + stockSymbol +
                "&apikey=Q5D084P5R0KIKU10";
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


    /*private static void fetchStockSearchResult(String stockSymbol) {
        JSONPlaceholder jsonPlaceholder = buildJSONPlaceholder();
        Call<StockSearchResult> call = jsonPlaceholder.getStockSearchResult(SYMBOL_SEARCH, stockSymbol, API_KEY);
        call.enqueue(new Callback<StockSearchResult>() {
            @Override
            public void onResponse(Call<StockSearchResult> call, Response<StockSearchResult> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                StockSearchResult stockSearchResult = response.body();
                List<StockSearchMatch> stockSearchMatchList = stockSearchResult.getBestMatches();

                if (stockSearchMatchList == null) {
                } else {
                    StockSearchMatch topMatch = stockSearchMatchList.get(0);
                    String stockType = topMatch.getType();

                }

            }

            @Override
            public void onFailure(Call<StockSearchResult> call, Throwable t) {

            }
        });
    }*/
}