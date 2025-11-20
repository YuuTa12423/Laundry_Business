package com.example.laundrybusiness;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class PlaceOrderActivity extends AppCompatActivity {

    // ALL UI COMPONENTS DECLARED
    TextInputEditText streetBarangay, city, zipCode, specialInstructions, pickUpDate, pickUpTime, weightEstimate, gcashReference;
    RadioGroup serviceTypeGroup, schedulingGroup, dryingGroup, foldingGroup, paymentMethodGroup;
    TextInputLayout textInputLayoutGcashRef; // To control visibility of G-Cash input
    Spinner detergentSpinner;
    CheckBox softenerCheck;
    Button submitOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_order);

        // This line requires the root view of activity_place_order.xml to have android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- 1. INITIALIZE VIEWS ---
        streetBarangay = findViewById(R.id.streetBarangay);
        city = findViewById(R.id.city);
        zipCode = findViewById(R.id.zipCode);
        specialInstructions = findViewById(R.id.specialInstructions);

        // Date/Time fields
        pickUpDate = findViewById(R.id.pickUpDate);
        pickUpTime = findViewById(R.id.pickUpTime);

        // New order preferences
        weightEstimate = findViewById(R.id.weightEstimate);
        dryingGroup = findViewById(R.id.dryingGroup);
        foldingGroup = findViewById(R.id.foldingGroup);

        // Radio Groups, Checkbox, Spinner, Button
        serviceTypeGroup = findViewById(R.id.serviceTypeGroup);
        schedulingGroup = findViewById(R.id.schedulingGroup);
        detergentSpinner = findViewById(R.id.detergentSpinner);
        softenerCheck = findViewById(R.id.softenerCheck);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        // Payment views
        gcashReference = findViewById(R.id.gcashReference);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        textInputLayoutGcashRef = findViewById(R.id.textInputLayoutGcashRef);


        // --- 2. UX ENHANCEMENT: DATE/TIME PICKERS ---
        // Attach Date and Time Pickers to the input fields
        pickUpDate.setOnClickListener(v -> showDatePickerDialog());
        pickUpTime.setOnClickListener(v -> showTimePickerDialog());


        // --- 3. CONDITIONAL PAYMENT LOGIC ---
        // Toggle G-Cash Reference Input Visibility based on RadioGroup selection
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.paymentGcash) {
                textInputLayoutGcashRef.setVisibility(View.VISIBLE);
            } else {
                textInputLayoutGcashRef.setVisibility(View.GONE);
                gcashReference.setText(""); // Clear input when switching back to COP
            }
        });


        // --- 4. SUBMISSION LOGIC & VALIDATION ---
        submitOrderButton.setOnClickListener(v -> {

            // 4a. Basic Address Validation
            if (streetBarangay.getText().toString().trim().isEmpty() ||
                    city.getText().toString().trim().isEmpty() ||
                    zipCode.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill in the complete address", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4b. Service and Scheduling Validation
            if (serviceTypeGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a service type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (schedulingGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select an order scheduling option", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4c. Payment Validation (REQUIRED)
            if (paymentMethodGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4d. G-Cash Reference Validation (CONDITIONAL)
            if (paymentMethodGroup.getCheckedRadioButtonId() == R.id.paymentGcash) {
                String reference = gcashReference.getText().toString().trim();
                if (reference.isEmpty() || reference.length() < 13) {
                    Toast.makeText(this, "Please enter a valid 13-digit G-Cash Reference Number", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Determine final payment method for API submission
            String paymentMethod = (paymentMethodGroup.getCheckedRadioButtonId() == R.id.paymentGcash) ? "GCASH" : "COP";
            String referenceNumber = (paymentMethod.equals("GCASH")) ? gcashReference.getText().toString() : "";


            // TODO: Implement API submission logic here!

            Toast.makeText(this, "Order submitted via " + paymentMethod + ". (API submission needed)", Toast.LENGTH_SHORT).show();
            finish();  // Go back to MainActivity
        });
    }

    // Method to show Date Picker
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Display the selected date in YYYY-MM-DD format
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    pickUpDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Method to show Time Picker
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Display the selected time in 24h format HH:MM
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    pickUpTime.setText(time);
                }, hour, minute, true); // true for 24-hour format
        timePickerDialog.show();
    }
}