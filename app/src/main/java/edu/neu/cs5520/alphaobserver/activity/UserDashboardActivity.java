package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.adapter.StockAdaptor;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockSave;

public class UserDashboardActivity extends AppCompatActivity {
    private TextView greetingTime;
    private TextView greetingUsername;
    private String currentUser;
    private DatabaseReference dbRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private StockAdaptor stockAdaptor;
    private List<StockCard> stockCardList;
    private static final String GOOD_MORNING = "Good morning, ";
    private static final String GOOD_AFTERNOON = "Good afternoon, ";
    private static final String GOOD_EVENING = "Good evening, ";
    private static final String GOOD_NIGHT = "Good night, ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_page);

        Bundle data = getIntent().getExtras();
        currentUser = data.getString("USER_NAME");

        dbRef = FirebaseDatabase.getInstance().getReference().child("StockSave");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stockCardList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StockSave stockSave = snapshot.getValue(StockSave.class);
                    String username = stockSave.getUsername();
                    if (username.equals(currentUser)) {
                        String stockSymbol = stockSave.getSymbol();
                        String stockType = "TECH";
                        String stockPrice = "$165.12";
                        StockCard stockCard = new StockCard(stockSymbol, stockType, stockPrice);
                        stockCardList.add(stockCard);
                    }
                }
                createRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

//        add stocksave
//        dbRef.push().setValue(new StockSave("linni", "IBM"));
//        dbRef.push().setValue(new StockSave("linni", "TSCO.LON"));
//        dbRef.push().setValue(new StockSave("linni", "SHOP.TRT"));

        greetingTime = (TextView) findViewById(R.id.text_dashboard_greeting_time);
        greetingUsername = (TextView) findViewById(R.id.text_dashboard_greeting_username);

        greetingTime.setText(getCurrentTime());
        greetingUsername.setText(currentUser + "!");

    }

    private void createRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.user_dashboard_recycler_view);
        recyclerView.setHasFixedSize(true);
        stockAdaptor = new StockAdaptor(stockCardList);
        recyclerView.setAdapter(stockAdaptor);
        recyclerView.setLayoutManager(layoutManager);
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return GOOD_MORNING;
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return GOOD_AFTERNOON;
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return GOOD_EVENING;
        } else {
            return GOOD_NIGHT;
        }
    }
}