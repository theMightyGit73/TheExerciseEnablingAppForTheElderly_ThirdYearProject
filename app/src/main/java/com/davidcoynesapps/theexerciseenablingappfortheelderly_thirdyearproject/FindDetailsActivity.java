package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class FindDetailsActivity extends AppCompatActivity {

    private EditText weightEditText;
    private EditText heightEditText;
    private DatePicker birthdateDatePicker;
    private RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_details_screen);

        weightEditText = findViewById(R.id.weight_input);
        heightEditText = findViewById(R.id.height_input);
        birthdateDatePicker = findViewById(R.id.birthdate_picked);
        genderRadioGroup = findViewById(R.id.gender_radio_group);

        // Set up the birthdate date picker to limit the date range to between 110 and 18 years ago
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -110);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        birthdateDatePicker.setMinDate(minDate.getTimeInMillis());
        birthdateDatePicker.setMaxDate(maxDate.getTimeInMillis());

        // Set up the save button
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> saveDetails());
    }

    private void saveDetails() {
        String weightString = weightEditText.getText().toString();
        String heightString = heightEditText.getText().toString();
        int birthYear = birthdateDatePicker.getYear();
        int birthMonth = birthdateDatePicker.getMonth() + 1; // Add 1 because months are 0-indexed
        int birthDay = birthdateDatePicker.getDayOfMonth();
        String gender = ((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString();

        if (weightString.isEmpty() || heightString.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        float weight = Float.parseFloat(weightString);
        float height = Float.parseFloat(heightString);

        if (weight <= 0 || height <= 0) {
            Toast.makeText(this, "Weight and height must be positive numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new User object with the details and add it to the database
        User user = new User();
        user.setBirthdate(String.format("%02d/%02d/%d", birthDay, birthMonth, birthYear));
        user.setGender(gender);
        user.setWeight(weight);
        user.setHeight(height);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.updateUser(user);

        // Save details in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("birthdate", user.getBirthdate());
        editor.putString("gender", user.getGender());
        editor.putFloat("weight", user.getWeight());
        editor.putFloat("height", user.getHeight());
        editor.apply();

        // Start the MainActivity and finish this activity
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}

