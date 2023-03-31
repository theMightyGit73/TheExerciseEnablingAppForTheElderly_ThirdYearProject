package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mEmailEditText;
    private EditText mWeightEditText;
    private EditText mHeightEditText;
    private Button mSaveButton;
    private Button mLogoutButton;
    private String mUsername;
    private DatabaseHelper mDbHelper;

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

        mDbHelper = new DatabaseHelper(this);

        // Get the logged in user's username
        Intent intent = getIntent();
        mUsername = intent.getStringExtra("username");

        // Get the user's details from the database
        User user = mDbHelper.getLoggedInUser();

        // Set the initial values of the EditText fields
        mUsernameEditText.setText(user.getName());
        mPasswordEditText.setText(user.getPassword());
        mEmailEditText.setText(user.getEmail());
        mWeightEditText.setText(String.valueOf(user.getWeight()));
        mHeightEditText.setText(String.valueOf(user.getHeight()));


        // Add an OnClickListener to the mPasswordEditText to prompt the user to enter their old password
        mPasswordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an EditText for the user to enter their old password
                final EditText oldPasswordEditText = new EditText(SettingsActivity.this);
                oldPasswordEditText.setHint("Old password");

                // Create an AlertDialog to prompt the user to enter their old password
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setView(oldPasswordEditText)
                        .setTitle("Enter your old password")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Check if the old password matches the user's current password
                                String oldPassword = oldPasswordEditText.getText().toString();
                                if (oldPassword.equals(user.getPassword())) {
                                    // If the old password matches, allow the user to enter a new password
                                    mPasswordEditText.setEnabled(true);
                                    mPasswordEditText.requestFocus();
                                } else {
                                    // If the old password doesn't match, show an error message and reset the password EditText
                                    Toast.makeText(SettingsActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                    mPasswordEditText.setText("");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Reset the password EditText if the user cancels
                                mPasswordEditText.setText("");
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // Reset the password EditText if the user cancels
                                mPasswordEditText.setText("");
                            }
                        });

                // Show the AlertDialog
                builder.show();
            }
        });

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

                // Update the user's details in the database
                User updatedUser = new User();
                updatedUser.setName(newUsername);
                updatedUser.setPassword(newPassword);
                updatedUser.setEmail(newEmail);
                updatedUser.setWeight(Integer.parseInt(newWeight));
                updatedUser.setHeight(Integer.parseInt(newHeight));

                mDbHelper.updateUser(updatedUser);

                // Show a Toast to confirm the save
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        });

        /// Logout button click listener
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display an alert dialog asking the user if they're sure they want to log out
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Clear the SharedPreferences and go back to the WelcomeActivity
                                SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();

                                Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog, so do nothing
                            }
                        });
                // Create the AlertDialog object and show it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
