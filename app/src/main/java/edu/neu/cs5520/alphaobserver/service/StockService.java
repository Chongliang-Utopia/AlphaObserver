package edu.neu.cs5520.alphaobserver.service;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

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
import edu.neu.cs5520.alphaobserver.activity.StockDetailActivity;
import edu.neu.cs5520.alphaobserver.fragment.MonthFragment;
import edu.neu.cs5520.alphaobserver.fragment.WeekFragment;
import edu.neu.cs5520.alphaobserver.model.JSONPlaceholder;
import edu.neu.cs5520.alphaobserver.model.StockSearchMatch;
import edu.neu.cs5520.alphaobserver.model.StockSearchResult;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockService {
    private static final String SYMBOL_SEARCH = "SYMBOL_SEARCH";
    private static final String API_KEY = "42Z7B1OTYEEJQCOT";
    private static final String BASE_URL = "https://www.alphavantage.co/";

    static List<float[]> data;
    private static final String TAG = "WebServiceActivity";

    private static WeekFragment weekFrag;
    private static MonthFragment monthFrag;
    private static Handler handler;
    private static StockDetailActivity activity;

    private static float price;
    private static String currency;

    public static void setModel(WeekFragment weekFragment, MonthFragment monthFragment, Handler mainThreadHandler, StockDetailActivity stockDetailActivity) {
        weekFrag = weekFragment;
        monthFrag = monthFragment;
        handler = mainThreadHandler;
        activity = stockDetailActivity;
    }

    public static List<float[]> getData() {
        return data;
    }
    public static float getPrice() {
        return price;
    }
    public static String getCurrency() {
        return currency;
    }
    public static Activity getAct() {
        return activity;
    }

    public static void setData(final String stockSymbol)
    {
        fetchStockSearchResult(stockSymbol, price);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject response = fetchStockData(getDailyURl(stockSymbol));
                data = parseData(response);


                // Get currency.
                if (data.size() > 0) {
                    price = data.get(data.size()-1)[1];
                }


                Log.e(TAG, String.valueOf(data));

                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (data.isEmpty()) {
                                //Toast.makeText(MainActivity.this, "You may be requesting too often, remote API is not responding, please wait and try again...", Toast.LENGTH_LONG).show();
                            } else {
                                //activity.setStockPrice(data.get(data.size()-1)[1]);
                                weekFrag.setChart(data, activity);
                                weekFrag.setPrice(String.valueOf(price), activity.findViewById(R.id.stockPrice_week));
                                weekFrag.setPercentage(data, activity.findViewById(R.id.stockPercentage_week));
                                monthFrag.setChart(data, activity);
                                monthFrag.setPrice(String.valueOf(price), activity.findViewById(R.id.stockPrice_month));
                                monthFrag.setPercentage(data, activity.findViewById(R.id.stockPercentage_month));

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

    static List<float[]> parseData(JSONObject response) {

        //   response in the format of:
        //        Time Series (Daily):
        //            2022-03-01:
        //                4. close	"294.9500"
        List<float[]> data = new ArrayList<>();


        try {
            JSONObject timeSeries = response.getJSONObject("Time Series (Daily)");
            for (int i = 0; i < timeSeries.names().length(); i++) {

                String day = timeSeries.names().getString(i);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date)formatter.parse(day);
                long mills = date.getTime();

                JSONObject priceInfo = timeSeries.getJSONObject(day);
                float closePrice = Float.parseFloat(priceInfo.getString("4. close"));
                Log.e("STOCK INFO", day + ", " + closePrice);

                data.add(new float[] {timeSeries.names().length()-1-i, closePrice});

            }

            Collections.reverse(data);

        } catch (JSONException | ParseException e) {
            Log.e("JSONException", String.valueOf(e));
            e.printStackTrace();
        }


        return data;
    }

    static String getDailyURl(String stockSymbol) {
        return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + stockSymbol +
                "&apikey=8A21BM84MW62L4C0";
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


    private static void fetchStockSearchResult(String stockSymbol, float price) {
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
                    String stockCurrencyString = topMatch.getCurrency();
                    Log.e("CURRENCY", stockCurrencyString);
                    currency = stockCurrencyString;
                    weekFrag.setCurrency(stockCurrencyString, activity.findViewById(R.id.stockCurrency_week));
                    monthFrag.setCurrency( stockCurrencyString, activity.findViewById(R.id.stockCurrency_month));

                }

            }

            @Override
            public void onFailure(Call<StockSearchResult> call, Throwable t) {

            }
        });
    }
    private static JSONPlaceholder buildJSONPlaceholder() {
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