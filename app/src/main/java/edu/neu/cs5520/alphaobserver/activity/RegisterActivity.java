package edu.neu.cs5520.alphaobserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.activity.EntryActivity;

public class RegisterActivity extends AppCompatActivity {
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        backButton = (Button) findViewById(R.id.button_register_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage(EntryActivity.class);
            }
        });
    }

    private void openPage(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}