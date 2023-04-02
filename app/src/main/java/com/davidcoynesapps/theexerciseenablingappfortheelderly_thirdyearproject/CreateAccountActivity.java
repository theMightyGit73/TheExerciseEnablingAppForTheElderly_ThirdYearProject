package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    private static final int EMAIL_VERIFICATION_REQUEST_CODE = 123;

    private Button buttonSave;
    private Button buttonVerifyEmail;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String verificationCode;
    private boolean isEmailVerified;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);

        emailEditText = findViewById(R.id.email_input);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        confirmPasswordEditText = findViewById(R.id.confirm_password_input);
        buttonSave = findViewById(R.id.saveButton_create);
        buttonVerifyEmail = findViewById(R.id.verify_email_button);

        dbHelper = new DatabaseHelper(this);

        buttonSave.setEnabled(false);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailVerified) {
                    // Get the email, username, password and confirm password from the EditText fields
                    email = emailEditText.getText().toString();
                    username = usernameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    confirmPassword = confirmPasswordEditText.getText().toString();

                    // Validate password format
                    Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
                    Matcher matcher = pattern.matcher(password);
                    if (!matcher.matches()) {
                        Toast.makeText(CreateAccountActivity.this, "Password must contain at least 1 lowercase, 1 uppercase, 1 digit, 1 special character and be at least 8 characters long", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validate that the password and confirm password inputs match
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(CreateAccountActivity.this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User user = new User();
                    user.setEmail(email);
                    user.setName(username);
                    user.setPassword(password);
                    dbHelper.addUser(user);

                    // Save the email, username and password to SharedPreferences
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CreateAccountActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("EMAIL", email);
                    editor.putString("USERNAME", username);
                    editor.putString("PASSWORD", password);
                    editor.apply();

                    // Start the FindDetailsActivity with the username in the intent
                    Intent intent = new Intent(CreateAccountActivity.this, FindDetailsActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Please verify your email before creating an account", Toast.LENGTH_SHORT).show();
                }
            }
        });


        buttonVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the email address from the EditText field
                email = emailEditText.getText().toString();

                // Validate email format
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(CreateAccountActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send email verification
                sendEmailVerification(email);
            }
        });

    }

    private void sendEmailVerification(String emailToVerify) {
        // Generate a random verification code
        String verificationCode = generateVerificationCode();

        // Store the verification code in the class variable for later reference
        this.verificationCode = verificationCode;

        // Create the email subject and message
        String subject = "Verify your email address";
        String message = "Please click the following link to verify your email address: <a href='http://www.example.com/verify_email?code="
                + verificationCode + "'>Verify Email</a>";

        // Create an intent to send the email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emailToVerify, null));

        // Add the email subject and message to the intent
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Set the email message type to text/html
        emailIntent.setType("text/html");

        // Start an activity to allow the user to choose an email client to send the email
        startActivityForResult(Intent.createChooser(emailIntent, "Send email..."), EMAIL_VERIFICATION_REQUEST_CODE);
    }


    // This method will be called after the user has clicked the email verification link
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EMAIL_VERIFICATION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the verification code in the URL matches the generated verification code
            Uri uri = data.getData();
            String verificationCode = uri.getQueryParameter("code");

            if (verificationCode != null && verificationCode.equals(this.verificationCode)) {
                // Email address has been verified successfully
                isEmailVerified = true;
            }
        }
    }
    private String generateVerificationCode() {
        // Generate a random string of characters to use as the verification code
        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 10;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return sb.toString();
    }

}
