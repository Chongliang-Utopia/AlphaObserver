package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import edu.neu.cs5520.alphaobserver.R;

public class CompanyDescriptionActivity extends AppCompatActivity {


    String stockSymbol;
    String description;

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
        setContentView(R.layout.activity_company_description);

        Bundle data = getIntent().getExtras();
        stockSymbol = data.getString("STOCK_SYMBOL");
        description = data.getString("DESCRIPTION");
        if (description.endsWith(" (?")) {
            description = description.substring(0, description.lastIndexOf(" (?"));
        }
        if (!description.endsWith(".")) {
            description += ".";
        }

        TextView stockSymbolText = findViewById(R.id.stockSymbol);
        stockSymbolText.setText(stockSymbol);

        ScrollView scrollView = findViewById(R.id.SCROLLER_ID);
        TextView descriptiontv = findViewById(R.id.description);
        descriptiontv.setText(description);

    }


    }
