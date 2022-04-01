package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.adapter.ReviewAdapter;
import edu.neu.cs5520.alphaobserver.adapter.StockAdaptor;
import edu.neu.cs5520.alphaobserver.model.StockCard;
import edu.neu.cs5520.alphaobserver.model.StockReview;
import edu.neu.cs5520.alphaobserver.model.StockSave;


public class ReviewActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String currentUser;
    private String stockSymbol;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ReviewAdapter stockAdaptor;
    private List<StockReview> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Bundle data = getIntent().getExtras();
        currentUser = data.getString("USER_NAME");

        stockSymbol = "PINS";

        mDatabase = FirebaseDatabase.getInstance().getReference();
       // DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Review").child(stockSymbol).push();
       // Task t = myRef.setValue(new StockReview(currentUser, stockSymbol, "This stock is good! \nThis stock is good!", System.currentTimeMillis()));
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Review").child(stockSymbol);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StockReview stockReview = snapshot.getValue(StockReview.class);
                    String username = stockReview.getUsername();
                    if (username.equals(currentUser)) {
                        reviewList.add(stockReview);
                    }
                }
                createRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void createRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.review_recycler_view);
        recyclerView.setHasFixedSize(true);
        stockAdaptor = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(stockAdaptor);
        recyclerView.setLayoutManager(layoutManager);
    }
}