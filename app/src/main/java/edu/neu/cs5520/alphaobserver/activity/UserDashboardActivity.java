package edu.neu.cs5520.alphaobserver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import edu.neu.cs5520.alphaobserver.R;

public class UserDashboardActivity extends AppCompatActivity {
    private TextView greetingTime;
    private TextView greetingUsername;
    private String currentUser;
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

        greetingTime = (TextView) findViewById(R.id.text_dashboard_greeting_time);
        greetingUsername = (TextView) findViewById(R.id.text_dashboard_greeting_username);

        greetingTime.setText(getCurrentTime());
        greetingUsername.setText(currentUser + "!");

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