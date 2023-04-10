package com.davidcoynesapps.theexerciseenablingappfortheelderly_thirdyearproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
        String message = "Please click the following link to verify your email address: http://www.example.com/verify_email?code=" + verificationCode;

        // Use the Sendinblue API to send the email
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("to", "[{\"email\":\"" + emailToVerify + "\"}]")
                .add("subject", subject)
                .add("htmlContent", message)
                .add("sender", "{\"name\":\"Your App\",\"email\":\"verify@example.com\"}")
                .build();
        Request request = new Request.Builder()
                .url("https://api.sendinblue.com/v3/smtp/email")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("api-key", "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")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle network error
                Log.e(TAG, "Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                // Handle response from Sendinblue API
                if (response.isSuccessful()) {
                    // Email sent successfully
                    Toast.makeText(CreateAccountActivity.this, "enail sent", Toast.LENGTH_SHORT).show();
                } else {
                    // Email sending failed
                    Toast.makeText(CreateAccountActivity.this, "email didnt send", Toast.LENGTH_SHORT).show();
                }
                response.close();
            }
        });
    }

    // This method will be called after the user has clicked the email verification link
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EMAIL_VERIFICATION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Use the Sendinblue API to verify the email
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("email", email)
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.sendinblue.com/v3/contacts/" + email + "/attributes")
                    .put(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("api-key", "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")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    // Handle response from Sendinblue API
                    if (response.isSuccessful()) {
                        // Email address has been verified successfully
                        isEmailVerified = true;
                    } else {
                        // Email address is not valid
                    }
                    response.close();
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    // Handle network error
                    Log.e(TAG, "Network error: " + e.getMessage());
                }

            });
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
