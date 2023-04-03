package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mEmailEditText;
    private EditText mWeightEditText;
    private EditText mHeightEditText;
    private Spinner mWeightUnitSpinner;
    private Spinner mHeightUnitSpinner;
    private Button mSaveButton;
    private Button mLogoutButton;
    private ImageButton mBackButton;
    private String mUsername;
    private DatabaseHelper mDbHelper;
    private float mWeight;
    private float mHeight;
    private int mWeightUnitIndex;
    private int mHeightUnitIndex;

    private void setWeightAndHeightFieldsBasedOnUnits() {
        String[] weightUnits = getResources().getStringArray(R.array.weight_units);
        String[] heightUnits = getResources().getStringArray(R.array.height_units);
        double weight;
        double height;
        if (mWeightUnitIndex == 0) { // kg to pounds
            weight = mWeight * 2.20462;
        } else { // lbs to kg
            weight = mWeight * 0.453592;
        }
        if (mHeightUnitIndex == 0) { // m to feet
            height = mHeight * 3.28084;
        } else { // ft to m
            height = mHeight * 0.3048;
        }
        mWeightEditText.setText(String.format("%.1f", weight));
        mHeightEditText.setText(String.format("%.2f", height));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        // Find views by ID
        mUsernameEditText = findViewById(R.id.username_text);
        mPasswordEditText = findViewById(R.id.password_text);
        mEmailEditText = findViewById(R.id.email_text);
        mWeightEditText = findViewById(R.id.weight_input);
        mHeightEditText = findViewById(R.id.height_input);
        mWeightUnitSpinner = findViewById(R.id.weight_unit_spinner);
        mHeightUnitSpinner = findViewById(R.id.height_unit_spinner);
        mSaveButton = findViewById(R.id.save_button);
        mLogoutButton = findViewById(R.id.logout_button);
        mBackButton = findViewById(R.id.backButton);

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

        // Set the initial values of the unit spinners
        ArrayAdapter<CharSequence> weightUnitAdapter = ArrayAdapter.createFromResource(this, R.array.weight_units, android.R.layout.simple_spinner_item);
        weightUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeightUnitSpinner.setAdapter(weightUnitAdapter);
        String[] weightUnits = getResources().getStringArray(R.array.weight_units);
        for (int i = 0; i < weightUnits.length; i++) {
            if (weightUnits[i].equalsIgnoreCase(user.getWeightUnits())) {
                mWeightUnitSpinner.setSelection(i);
                mWeightUnitIndex = i;
                break;
            }
        }

        ArrayAdapter<CharSequence> heightUnitAdapter = ArrayAdapter.createFromResource(this, R.array.height_units, android.R.layout.simple_spinner_item);
        heightUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHeightUnitSpinner.setAdapter(heightUnitAdapter);
        String[] heightUnits = getResources().getStringArray(R.array.height_units);
        for (int i = 0; i < heightUnits.length; i++) {
            if (heightUnits[i].equalsIgnoreCase(user.getHeightUnits())) {
                mHeightUnitSpinner.setSelection(i);
                mHeightUnitIndex = i;
                break;
            }
        }

        // Set weight and height values based on selected unit
        mWeight = user.getWeight();
        mHeight = user.getHeight();

        // Set up listeners
        mWeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mWeightUnitIndex = i;
                setWeightAndHeightFieldsBasedOnUnits();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        mHeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mHeightUnitIndex = i;
                setWeightAndHeightFieldsBasedOnUnits();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

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
                // Get the new user details
                String username = mUsernameEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String weightStr = mWeightEditText.getText().toString().trim();
                String heightStr = mHeightEditText.getText().toString().trim();

                // Get the selected weight and height units
                String weightUnit = mWeightUnitSpinner.getSelectedItem().toString();
                String heightUnit = mHeightUnitSpinner.getSelectedItem().toString();

                // Validate the user details
                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                float weight;
                float height;
                try {
                    weight = Float.parseFloat(weightStr);
                    height = Float.parseFloat(heightStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(SettingsActivity.this, "Please enter valid values for weight and height", Toast.LENGTH_SHORT).show();
                    return;
                }

                /// Update the user's details in the database
                User updatedUser = new User();
                updatedUser.setName(username);
                updatedUser.setPassword(password);
                updatedUser.setEmail(email);
                updatedUser.setWeight(weight);
                updatedUser.setWeightUnits(weightUnit);
                updatedUser.setHeight(height);
                updatedUser.setHeightUnits(heightUnit);

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
                                editor.putBoolean("has_seen_welcome_screen", false); // clear the has_seen_welcome_screen preference
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
