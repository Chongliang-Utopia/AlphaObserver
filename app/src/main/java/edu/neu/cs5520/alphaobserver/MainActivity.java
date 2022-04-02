package edu.neu.cs5520.alphaobserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.neu.cs5520.alphaobserver.activity.EntryActivity;
import edu.neu.cs5520.alphaobserver.activity.StockDetailActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, UserDashboardActivity.class);
//        intent.putExtra("USER_NAME", "linni");
//        startActivity(intent);


//        Intent intent = new Intent(this, StockDetailActivity.class);
//        intent.putExtra("USER_NAME", "tcl");
//        startActivity(intent);


        Intent intent = new Intent(this, StockDetailActivity.class);
        startActivity(intent);
    }
}