package edu.neu.cs5520.alphaobserver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import edu.neu.cs5520.alphaobserver.R;
import edu.neu.cs5520.alphaobserver.model.User;

public class LoginActivity extends AppCompatActivity {
    private Button backButton;
    private MaterialButton loginButton;
    private EditText account;
    private EditText password;
    private String accountString;
    private String passwordString;
    private String currentUser;
    private DatabaseReference dbRef;
    private boolean validAccount;
    private boolean validPassword;
    private Map<String, String> usernamePasswordMap;
    private Map<String, String> usernameEmailMap;
    private Map<String, String> emailUsernameMap;
    private final static String EMPTY_ACCOUNT = "Empty username or email!";
    private final static String EMPTY_PASSWORD = "Empty password!";
    private final static String NON_EXISTED_ACCOUNT = "The account doesn't exist!";
    private final static String PASSWORD_NOT_MATCH = "The password doesn't match!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        usernamePasswordMap = new HashMap<>();
        usernameEmailMap = new HashMap<>();
        emailUsernameMap = new HashMap<>();

        dbRef = FirebaseDatabase.getInstance().getReference().child("User");

        account = (EditText) findViewById(R.id.text_login_account_value);
        password = (EditText) findViewById(R.id.text_login_password_value);

        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                validAccount = false;
                accountString = account.getText().toString().trim();
                if (TextUtils.isEmpty(accountString)) {
                    account.setError(EMPTY_ACCOUNT);
                } else if (!usernameEmailMap.containsValue(accountString) &&
                           !usernameEmailMap.containsKey(accountString)) {
                    account.setError(NON_EXISTED_ACCOUNT);
                } else {
                    validAccount = true;
                    currentUser = accountString;
                    if (emailUsernameMap.containsKey(accountString)) {
                        currentUser = emailUsernameMap.get(accountString);
                    }
                    checkValidLogin();
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
                    checkValidLogin();
                }
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernamePasswordMap = new HashMap<>();
                usernameEmailMap = new HashMap<>();
                emailUsernameMap = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String emailRecord = user.getEmail();
                    String usernameRecord = user.getUsername();
                    String passwordRecord = user.getPassword();
                    usernamePasswordMap.put(usernameRecord, passwordRecord);
                    usernameEmailMap.put(usernameRecord, emailRecord);
                    emailUsernameMap.put(emailRecord, usernameRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backButton = (Button) findViewById(R.id.button_login_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPage(EntryActivity.class);
            }
        });

        loginButton = (MaterialButton) findViewById(R.id.button_login);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernamePasswordMap.get(currentUser).equals(passwordString)) {
                    openPageWithUser(UserDashboardActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, PASSWORD_NOT_MATCH, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openPage(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void openPageWithUser(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("USER_NAME", currentUser);
        startActivity(intent);
    }

    private void checkValidLogin() {
        if (validAccount && validPassword) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        account.setText("");
        password.setText("");
    }
}