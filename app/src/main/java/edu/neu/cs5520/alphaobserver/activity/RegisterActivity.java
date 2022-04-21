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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
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
    private MaterialButton registerButton;
    private EditText email;
    private EditText username;
    private EditText password;
    private DatabaseReference dbRef;
    private String emailString;
    private String usernameString;
    private String passwordString;
    private boolean validEmail;
    private boolean validUsername;
    private boolean validPassword;
    private Set<String> existedEmails;
    private Set<String> existedUsernames;
    private final static String EXISTED_EMAIL_ADDRESS = "Existed email address!";
    private final static String EXISTED_USERNAME = "Existed username!";
    private final static String EMPTY_USERNAME = "Empty username!";
    private final static String EMPTY_EMAIL_ADDRESS = "Empty email address!";
    private final static String INVALID_EMAIL_ADDRESS = "Invalid email address!";
    private final static String EMPTY_PASSWORD = "Empty password!";
    private final static String REGISTER_SUCESS = "Registered successfully!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        dbRef = FirebaseDatabase.getInstance().getReference().child("User");

        email = (EditText) findViewById(R.id.text_register_email_value);
        username = (EditText) findViewById(R.id.text_register_username_value);
        password = (EditText) findViewById(R.id.text_register_password_value);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                validEmail = false;
                emailString = email.getText().toString().trim();
                if (TextUtils.isEmpty(emailString)) {
                    email.setError(EMPTY_EMAIL_ADDRESS);
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    email.setError(INVALID_EMAIL_ADDRESS);
                } else if (existedEmails.contains(emailString)) {
                    email.setError(EXISTED_EMAIL_ADDRESS);
                } else {
                    validEmail = true;
                    checkValidRegister();
                }
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                validUsername = false;
                usernameString = username.getText().toString().trim();
                if (TextUtils.isEmpty(usernameString)) {
                    username.setError(EMPTY_USERNAME);
                } else if (existedEmails.contains(emailString)) {
                    username.setError(EXISTED_USERNAME);
                } else {
                    validUsername = true;
                    checkValidRegister();
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                validPassword = false;
                passwordString = password.getText().toString().trim();
                if (TextUtils.isEmpty(passwordString)) {
                    password.setError(EMPTY_PASSWORD);
                } else {
                    validPassword = true;
                    checkValidRegister();
                }
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                existedEmails = new HashSet<>();
                existedUsernames = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    existedEmails.add(user.getEmail());
                    existedUsernames.add(user.getUsername());
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

        registerButton = (MaterialButton) findViewById(R.id.button_register);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(emailString, usernameString, passwordString);
                dbRef.push().setValue(user);
                Toast.makeText(RegisterActivity.this, REGISTER_SUCESS, Toast.LENGTH_SHORT).show();
                openPage(EntryActivity.class);
            }
        });
    }

    private void openPage(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void checkValidRegister() {
        if (validEmail && validPassword && validUsername) {
            registerButton.setEnabled(true);
        } else {
            registerButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        email.setText("");
        username.setText("");
        password.setText("");
    }
}