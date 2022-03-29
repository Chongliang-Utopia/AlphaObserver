package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.User;

public class RegisterActivity extends AppCompatActivity {
    private Button backButton;
    private EditText email;
    private EditText username;
    private EditText password;
    private DatabaseReference dbRef;
    private String emailString;
    private Set<String> existedEmails;
    private final static String EXISTED_EMAIL_ADDRESS = "Existed email address!";
    private final static String EMPTY_EMAIL_ADDRESS = "Empty email address!";
    private final static String INVALID_EMAIL_ADDRESS = "Invalid email address!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        dbRef = FirebaseDatabase.getInstance().getReference().child("User");

        email = (EditText) findViewById(R.id.text_register_email_value);
        username = (EditText) findViewById(R.id.text_register_username_value);
        password = (EditText) findViewById(R.id.text_register_password_value);
        existedEmails = new HashSet<>();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailString = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailString)) {
                    email.setError(EMPTY_EMAIL_ADDRESS);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    email.setError(INVALID_EMAIL_ADDRESS);
                } else if (existedEmails.contains(emailString)) {
                    email.setError(EXISTED_EMAIL_ADDRESS);
                }
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String emailRecord = user.getEmail();
                    existedEmails.add(emailRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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