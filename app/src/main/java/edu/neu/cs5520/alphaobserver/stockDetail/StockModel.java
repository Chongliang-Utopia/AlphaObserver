package edu.neu.cs5520.alphaobserver.stockDetail;

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

public class StockModel {

    static List<float[]> data;
    private static final String TAG = "WebServiceActivity";

    private static WeekFragment bar;
    private static MonthFragment foo;
    private static Handler handler;

    public static void setModel(WeekFragment weekFragment, MonthFragment monthFragment, Handler mainThreadHandler) {
        bar = weekFragment;
        foo = monthFragment;
        handler = mainThreadHandler;
    }

    public static List<float[]> getData() {
        return data;
    }

    public static void setData(final String stockSymbol)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject response = fetchStockData(getDailyURl(stockSymbol));
                data = parseData(response);

                Log.e(TAG, String.valueOf(data));

                try {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (data.isEmpty()) {
                                //Toast.makeText(MainActivity.this, "You may be requesting too often, remote API is not responding, please wait and try again...", Toast.LENGTH_LONG).show();
                            } else {
                                //createChart(data);
//                                bar.setText("New Text");
//                                foo.setText("New Text");
                                bar.setChart(data);

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

}