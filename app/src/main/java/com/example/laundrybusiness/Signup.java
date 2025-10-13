package com.example.laundrybusiness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Signup extends AppCompatActivity {

    TextInputEditText textInputEditTextFullname, textInputEditTextAddress, textInputEditTextUsername,
            textInputEditTextPassword, textInputEditTextEmail;
    CheckBox checkboxTerms;
    ImageView backArrow;
    TextView textViewSignIn;
    ProgressBar progressBar;
    android.widget.Button buttonCreateAccount;  // Fixed ID to match XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views (added missing ones like address, checkbox, backArrow, etc.)
        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextAddress = findViewById(R.id.address);  // Added for address field
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextEmail = findViewById(R.id.email);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);  // Fixed ID to match XML
        checkboxTerms = findViewById(R.id.checkboxTerms);  // Added for terms validation
        backArrow = findViewById(R.id.backArrow);  // Added for back navigation
        textViewSignIn = findViewById(R.id.textViewSignIn);  // Fixed ID to match XML
        progressBar = findViewById(R.id.progress);  // Now exists in updated XML

        // Back arrow click listener (added for navigation back to previous screen, e.g., Login)
        backArrow.setOnClickListener(v -> finish());

        // Sign In TextView click listener (added for connectivity to Login activity)
        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();  // Optional: Closes Signup to prevent back navigation stack issues
        });

        // Create Account button click listener (full logic with validation and HTTP)
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values and trim whitespace
                String fullname = textInputEditTextFullname.getText().toString().trim();
                String address = textInputEditTextAddress.getText().toString().trim();
                String username = textInputEditTextUsername.getText().toString().trim();
                String password = textInputEditTextPassword.getText().toString().trim();
                String email = textInputEditTextEmail.getText().toString().trim();

                // Validation: Check if all fields are non-empty and terms are agreed
                if (!fullname.isEmpty() && !address.isEmpty() && !username.isEmpty() &&
                        !password.isEmpty() && !email.isEmpty() && checkboxTerms.isChecked()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Prepare fields and data for HTTP POST (now 5 fields including address)
                            String[] field = new String[5];
                            field[0] = "fullname";
                            field[1] = "address";  // Added
                            field[2] = "username";
                            field[3] = "email";
                            field[4] = "password";

                            String[] data = new String[5];
                            data[0] = fullname;
                            data[1] = address;  // Added
                            data[2] = username;
                            data[3] = email;
                            data[4] = password;

                            // Execute HTTP request using PutData library
                            PutData putData = new PutData("http://192.168.0.103/LaundryServiceLogInRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if (result.equals("Sign Up Success")) {  // Adjust based on your PHP response
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();  // Close Signup after success
                                    } else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Signup Failed: Check connection", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show specific error messages for better UX
                    if (!checkboxTerms.isChecked()) {
                        Toast.makeText(getApplicationContext(), "Please agree to the Terms of Service", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}