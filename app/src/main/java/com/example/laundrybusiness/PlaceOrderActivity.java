package com.example.laundrybusiness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class PlaceOrderActivity extends AppCompatActivity {

    TextInputEditText streetBarangay, city, zipCode, specialInstructions, pickUpDate, pickUpTime;
    RadioGroup serviceTypeGroup, schedulingGroup;
    Spinner detergentSpinner;
    CheckBox softenerCheck;
    Button submitOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        streetBarangay = findViewById(R.id.streetBarangay);
        city = findViewById(R.id.city);
        zipCode = findViewById(R.id.zipCode);
        specialInstructions = findViewById(R.id.specialInstructions);
        pickUpDate = findViewById(R.id.pickUpDate);
        pickUpTime = findViewById(R.id.pickUpTime);
        serviceTypeGroup = findViewById(R.id.serviceTypeGroup);
        schedulingGroup = findViewById(R.id.schedulingGroup);
        detergentSpinner = findViewById(R.id.detergentSpinner);
        softenerCheck = findViewById(R.id.softenerCheck);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        // Submit button click listener
        submitOrderButton.setOnClickListener(v -> {
            // Basic validation
            if (streetBarangay.getText().toString().trim().isEmpty() ||
                    city.getText().toString().trim().isEmpty() ||
                    zipCode.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill in the complete address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serviceTypeGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a service type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (schedulingGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select an order scheduling option", Toast.LENGTH_SHORT).show();
                return;
            }

            // For now, just show a success toast and go back (you can add server submission later)
            Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_SHORT).show();
            finish();  // Go back to MainActivity
        });
    }
}