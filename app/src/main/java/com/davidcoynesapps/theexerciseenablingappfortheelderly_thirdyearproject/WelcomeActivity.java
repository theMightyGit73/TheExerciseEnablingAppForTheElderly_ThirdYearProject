package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if the user has already seen the Welcome screen
        boolean hasSeenWelcomeScreen = sharedPreferences.getBoolean("has_seen_welcome_screen", false);
        if (hasSeenWelcomeScreen) {
            // User has already seen the Welcome screen, proceed to main activity
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Finish the WelcomeActivity so the user can't go back to it
        } else {
            // User hasn't seen the Welcome screen yet, show it
            setContentView(R.layout.activity_welcome_screen);

            Button signInButton = findViewById(R.id.sign_in_button);
            Button createAccountButton = findViewById(R.id.create_account_button);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            createAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                }
            });

            // Update the SharedPreferences to indicate that the user has seen the Welcome screen
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("has_seen_welcome_screen", true);
            editor.apply();
        }
    }
}
