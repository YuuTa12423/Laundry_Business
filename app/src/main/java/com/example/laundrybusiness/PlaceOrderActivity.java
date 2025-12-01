package com.example.laundrybusiness;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler; // ADDED
import android.os.Looper; // ADDED
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton; // ADDED to get selected radio button text
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
import com.vishnusivadas.advanced_httpurlconnection.PutData; // ADDED

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


        // --- 3. SUBMISSION LOGIC: ONLY ADD ORDER ON SUCCESSFUL COMPLETION (MODIFIED) ---
        submitOrderButton.setOnClickListener(v -> {

            // --- 3a. Validation Checks (Return if any fails) ---
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

            // --- 3b. Data Collection (Collect all form inputs) ---
            String fullAddress = streetBarangay.getText().toString() + ", " + city.getText().toString() + ", " + zipCode.getText().toString();
            String serviceType = ((RadioButton) findViewById(serviceTypeGroup.getCheckedRadioButtonId())).getText().toString();
            String weight = weightEstimate.getText().toString().trim();
            String detergent = detergentSpinner.getSelectedItem().toString();
            String hasSoftener = softenerCheck.isChecked() ? "Yes" : "No";
            String dryingPref = (dryingGroup.getCheckedRadioButtonId() != -1) ? ((RadioButton) findViewById(dryingGroup.getCheckedRadioButtonId())).getText().toString() : "Not Specified";
            String foldingPref = (foldingGroup.getCheckedRadioButtonId() != -1) ? ((RadioButton) findViewById(foldingGroup.getCheckedRadioButtonId())).getText().toString() : "Not Specified";
            String scheduling = ((RadioButton) findViewById(schedulingGroup.getCheckedRadioButtonId())).getText().toString();
            String dateTime = pickUpDate.getText().toString() + " " + pickUpTime.getText().toString();
            String paymentMethod = (paymentMethodGroup.getCheckedRadioButtonId() == R.id.paymentGcash) ? "GCASH" : "COP";
            String referenceNumber = (paymentMethod.equals("GCASH")) ? gcashReference.getText().toString() : "N/A";
            String instructions = specialInstructions.getText().toString().trim();


            // --- 3c. Execute Network Submission (Fills the TODO) ---

            // Set up the network call on a background thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                // You must update the URL to your actual server path!
                String url = "http://172.17.12.249/LaundryServiceLogInRegister/order_submission.php";

                String[] field = new String[12];
                field[0] = "address";
                field[1] = "service_type";
                field[2] = "weight_estimate";
                field[3] = "detergent";
                field[4] = "softener";
                field[5] = "drying_pref";
                field[6] = "folding_pref";
                field[7] = "scheduling";
                field[8] = "pickup_datetime";
                field[9] = "payment_method";
                field[10] = "reference_number";
                field[11] = "instructions";

                String[] data = new String[12];
                data[0] = fullAddress;
                data[1] = serviceType;
                data[2] = weight;
                data[3] = detergent;
                data[4] = hasSoftener;
                data[5] = dryingPref;
                data[6] = foldingPref;
                data[7] = scheduling;
                data[8] = dateTime;
                data[9] = paymentMethod;
                data[10] = referenceNumber;
                data[11] = instructions;

                PutData putData = new PutData(url, "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.startsWith("Order Success")) { // Assume server returns "Order Success #123"
                            Toast.makeText(getApplicationContext(), "Order submitted successfully! " + result, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Submission Failed: " + result, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Submission Failed: Check server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Connection Error or Script Not Found", Toast.LENGTH_SHORT).show();
                }
            });
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