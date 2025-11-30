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
    android.widget.Button buttonCreateAccount;

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

        // Initialize views
        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextAddress = findViewById(R.id.address);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextEmail = findViewById(R.id.email);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        checkboxTerms = findViewById(R.id.checkboxTerms);
        backArrow = findViewById(R.id.backArrow);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        progressBar = findViewById(R.id.progress);

        // Back arrow click listener (navigates back to Login)
        backArrow.setOnClickListener(v -> finish());

        // "Already have an account? Sign In" click listener (navigates to Login)
        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();  // Close Signup
        });

        // Create Account button click listener
        buttonCreateAccount.setOnClickListener(v -> {
            String fullname = textInputEditTextFullname.getText().toString().trim();
            String address = textInputEditTextAddress.getText().toString().trim();
            String username = textInputEditTextUsername.getText().toString().trim();
            String password = textInputEditTextPassword.getText().toString().trim();
            String email = textInputEditTextEmail.getText().toString().trim();

            boolean fieldsFilled = !fullname.isEmpty() && !address.isEmpty() && !username.isEmpty() &&
                    !password.isEmpty() && !email.isEmpty();
            boolean termsChecked = checkboxTerms.isChecked(); // Validation for Checkbox

            if (fieldsFilled && termsChecked) {
                // If all fields are filled and terms are checked, proceed with signup
                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String[] field = new String[5];
                    field[0] = "fullname";
                    field[1] = "address";
                    field[2] = "username";
                    field[3] = "email";
                    field[4] = "password";

                    String[] data = new String[5];
                    data[0] = fullname;
                    data[1] = address;
                    data[2] = username;
                    data[3] = email;
                    data[4] = password;

                    PutData putData = new PutData("http://172.17.12.249/LaundryServiceLogInRegister/signup.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            progressBar.setVisibility(View.GONE);
                            String result = putData.getResult();
                            if (result.equals("Sign Up Success")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
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
                });
            } else {
                // Handle missing fields or unchecked terms
                if (!fieldsFilled) {
                    Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                } else if (!termsChecked) {
                    Toast.makeText(getApplicationContext(), "Please agree to the Terms of Service", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}