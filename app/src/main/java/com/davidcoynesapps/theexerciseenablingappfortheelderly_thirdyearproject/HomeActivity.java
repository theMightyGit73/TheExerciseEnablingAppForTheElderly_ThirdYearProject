package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Get the saved username from the Intent extras
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");

        // Get references to UI elements
        usernameText = findViewById(R.id.username_text);
        Button settingsButton = findViewById(R.id.settings_button);
        Button weatherButton = findViewById(R.id.weather_button);
        Button calendarButton = findViewById(R.id.calendar_button);
        Button stepCounterButton = findViewById(R.id.stepcounter_button);

        // Set the saved username in the username text field
        usernameText.setText(username);

        // Set up click listeners for the buttons to navigate to other screens
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        stepCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StepCounterActivity.class);
                startActivity(intent);
            }
        });
    }
}