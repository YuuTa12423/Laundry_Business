package com.example.laundrybusiness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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

public class Login extends AppCompatActivity {

    TextInputEditText textInputEditTextUsername, textInputEditTextPassword;
    ProgressBar progressBar;
    TextView signUpText;
    android.widget.Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progress);
        signUpText = findViewById(R.id.signUpText);

        // "Sign up here" click listener (navigates to Signup)
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Signup.class);
            startActivity(intent);
            finish();  // Close Login
        });

        // Login button click listener
        buttonLogin.setOnClickListener(v -> {
            String username = textInputEditTextUsername.getText().toString().trim();
            String password = textInputEditTextPassword.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "password";

                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = password;

                    // WARNING: Using HTTP for login is insecure!
                    PutData putData = new PutData("http://172.17.12.249/LaundryServiceLogInRegister/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            progressBar.setVisibility(View.GONE);
                            String result = putData.getResult();
                            if (result.equals("Login Success")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                // --- CRITICAL NAVIGATION BLOCK ---
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("username", username); // PASS USERNAME
                                startActivity(intent);                 // LAUNCH MAIN ACTIVITY
                                finish();                              // CLOSE LOGIN PAGE
                                // ---------------------------------

                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Login Failed: Check connection", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
            }
        });
    }
}