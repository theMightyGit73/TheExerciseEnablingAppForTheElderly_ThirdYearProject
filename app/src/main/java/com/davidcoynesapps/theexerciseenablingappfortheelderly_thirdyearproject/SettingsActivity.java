package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mEmailEditText;
    private EditText mWeightEditText;
    private EditText mHeightEditText;
    private Button mSaveButton;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        // Find views by ID
        mUsernameEditText = findViewById(R.id.username_text);
        mPasswordEditText = findViewById(R.id.password_text);
        mEmailEditText = findViewById(R.id.email_text);
        mWeightEditText = findViewById(R.id.weight_text);
        mHeightEditText = findViewById(R.id.height_text);
        mSaveButton = findViewById(R.id.save_button);
        mLogoutButton = findViewById(R.id.logout_button);

        // Set initial values
        mUsernameEditText.setText(getIntent().getStringExtra("username"));
        mPasswordEditText.setText(getIntent().getStringExtra("password"));
        mEmailEditText.setText(getIntent().getStringExtra("email"));
        mWeightEditText.setText(getIntent().getStringExtra("weight"));
        mHeightEditText.setText(getIntent().getStringExtra("height"));

        // Save button click listener
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new values from the EditTexts
                String newUsername = mUsernameEditText.getText().toString();
                String newPassword = mPasswordEditText.getText().toString();
                String newEmail = mEmailEditText.getText().toString();
                String newWeight = mWeightEditText.getText().toString();
                String newHeight = mHeightEditText.getText().toString();

                // Save the new values in SharedPreferences
                SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", newUsername);
                editor.putString("password", newPassword);
                editor.putString("email", newEmail);
                editor.putString("weight", newWeight);
                editor.putString("height", newHeight);
                editor.apply();

                // Show a Toast to confirm the save
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout button click listener
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the SharedPreferences and go back to the WelcomeActivity
                SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
