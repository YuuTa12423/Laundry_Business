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

    TextInputEditText streetBarangay, city, zipCode, specialInstructions, pickUpDate, pickUpTime, weightEstimate, gcashReference;
    RadioGroup serviceTypeGroup, schedulingGroup, dryingGroup, foldingGroup, paymentMethodGroup;
    TextInputLayout textInputLayoutGcashRef;
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

        // --- 1. INITIALIZE VIEWS (as before) ---
        streetBarangay = findViewById(R.id.streetBarangay);
        city = findViewById(R.id.city);
        zipCode = findViewById(R.id.zipCode);
        specialInstructions = findViewById(R.id.specialInstructions);
        pickUpDate = findViewById(R.id.pickUpDate);
        pickUpTime = findViewById(R.id.pickUpTime);
        weightEstimate = findViewById(R.id.weightEstimate);
        dryingGroup = findViewById(R.id.dryingGroup);
        foldingGroup = findViewById(R.id.foldingGroup);
        serviceTypeGroup = findViewById(R.id.serviceTypeGroup);
        schedulingGroup = findViewById(R.id.schedulingGroup);
        detergentSpinner = findViewById(R.id.detergentSpinner);
        softenerCheck = findViewById(R.id.softenerCheck);
        submitOrderButton = findViewById(R.id.submitOrderButton);
        gcashReference = findViewById(R.id.gcashReference);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        textInputLayoutGcashRef = findViewById(R.id.textInputLayoutGcashRef);

        // --- 2. UX ENHANCEMENT & CONDITIONAL LOGIC (as before) ---
        pickUpDate.setOnClickListener(v -> showDatePickerDialog());
        pickUpTime.setOnClickListener(v -> showTimePickerDialog());

        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.paymentGcash) {
                textInputLayoutGcashRef.setVisibility(View.VISIBLE);
            } else {
                textInputLayoutGcashRef.setVisibility(View.GONE);
                gcashReference.setText("");
            }
        });


        // --- 3. SUBMISSION LOGIC: ONLY ADD ORDER ON SUCCESSFUL COMPLETION ---
        submitOrderButton.setOnClickListener(v -> {

            // 3a. Validation Checks (Return if any fails)
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

            if (paymentMethodGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Conditional G-Cash Validation
            if (paymentMethodGroup.getCheckedRadioButtonId() == R.id.paymentGcash) {
                String reference = gcashReference.getText().toString().trim();
                if (reference.isEmpty() || reference.length() < 13) {
                    Toast.makeText(this, "Please enter a valid 13-digit G-Cash Reference Number", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // =========================================================================
            // 3b. SUCCESSFUL COMPLETION: EXECUTE THE ORDER ADDITION/SUBMISSION
            // =========================================================================

            // Determine data fields to be sent (Example of data gathering)
            String paymentMethod = (paymentMethodGroup.getCheckedRadioButtonId() == R.id.paymentGcash) ? "GCASH" : "COP";
            String referenceNumber = (paymentMethod.equals("GCASH")) ? gcashReference.getText().toString() : "";

            // 💡 TODO: Place your HTTP/Retrofit call here to send ALL collected data
            // (Address, Service, Preferences, Scheduling, Payment, Reference) to the server.
            // Only upon receiving a successful server response should you display the success Toast.


            // --- Placeholder for successful network call ---
            Toast.makeText(this, "Order #999 Added Successfully via " + paymentMethod + "!", Toast.LENGTH_LONG).show();

            // --- Final Action: Close the form ---
            finish();  // Go back to MainActivity
        });
    }

    // Date Picker Logic (Unchanged)
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    pickUpDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    // Time Picker Logic (Unchanged)
    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    pickUpTime.setText(time);
                }, hour, minute, true);
        timePickerDialog.show();
    }
}