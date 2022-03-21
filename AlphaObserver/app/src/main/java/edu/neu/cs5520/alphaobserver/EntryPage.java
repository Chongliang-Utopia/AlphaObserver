package edu.neu.cs5520.alphaobserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EntryPage extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);

        loginButton = (Button) findViewById(R.id.button_entry_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage(LoginPage.class);
            }
        });

        registerButton = (Button) findViewById(R.id.button_entry_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage(RegisterPage.class);
            }
        });
    }

    private void openPage(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}