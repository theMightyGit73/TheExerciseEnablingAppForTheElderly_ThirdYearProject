package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mUsernameEditText = findViewById(R.id.username_input);
        mPasswordEditText = findViewById(R.id.password_input);
        mLoginButton = findViewById(R.id.login_button);
        mDatabaseHelper = new DatabaseHelper(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the username and password entered by the user
                String username = mUsernameEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                // Check if the fields are empty
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter a username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the credentials match the ones stored in the database
                    boolean isLoginSuccessful = mDatabaseHelper.checkUserExists(username, password);
                    if (isLoginSuccessful) {
                        // Login successful
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
