package com.example.laundrybusiness;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private String orderId;
    private TextView orderDetailHeader;
    private RecyclerView recyclerTimeline;
    private Handler handler = new Handler();
    private Runnable trackingRunnable;

    // NOTE: This array simulates status data fetched from an API
    private static final List<String> STATUS_STEPS = Arrays.asList(
            "ORDER_PLACED", "PICKED_UP", "IN_WASH", "READY_FOR_PICKUP", "DELIVERED"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        orderDetailHeader = findViewById(R.id.orderDetailHeader);
        recyclerTimeline = findViewById(R.id.recyclerTimeline);

        // 1. Get the Order ID passed from the OrderAdapter
        if (getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getStringExtra("ORDER_ID");
            orderDetailHeader.setText(String.format(Locale.getDefault(), "Tracking Order %s", orderId));
        } else {
            Toast.makeText(this, "Error: No Order ID provided.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Setup the Timeline RecyclerView (Placeholder structure)
        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));

        // 3. Start the tracking update process (polling loop)
        startTrackingUpdates();
    }

    // Polling logic to simulate fetching status updates from the Admin Panel API
    private void startTrackingUpdates() {
        // --- This entire section is a simplified placeholder for API Polling ---

        trackingRunnable = new Runnable() {
            @Override
            public void run() {
                // In a real app, this would be a Retrofit call:
                // String currentStatus = API.fetchStatus(orderId);

                // For demonstration: We'll simulate status progression
                String currentStatus = getSimulatedStatus();

                // Update the RecyclerView/Timeline UI
                updateTimelineUI(currentStatus);

                // Continue polling only if the order is not fully delivered
                if (!currentStatus.equals("DELIVERED")) {
                    // Poll the API every 10 seconds (10000ms)
                    handler.postDelayed(this, 10000);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order " + orderId + " is COMPLETE.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        // Start the initial check immediately
        handler.post(trackingRunnable);
    }

    // Method to stop the tracking loop when the activity is closed
    @Override
    protected void onPause() {
        super.onPause();
        if (trackingRunnable != null) {
            handler.removeCallbacks(trackingRunnable);
        }
    }

    // --- Utility Methods for UI (Needs implementation of TimelineAdapter) ---
    private void updateTimelineUI(String currentStatus) {
        // Here you would instantiate your TimelineAdapter
        // with the list of status steps (STATUS_STEPS) and the current status.
        // For now, we'll just log the status.
        System.out.println("Current status for " + orderId + ": " + currentStatus);
    }

    // --- TEMPORARY SIMULATION METHOD (REMOVE IN PRODUCTION) ---
    private String getSimulatedStatus() {
        // Simulates the status moving from PLACED -> PICKED_UP, etc.
        // This is where a real API would return the true status.
        int index = (int) (System.currentTimeMillis() / 10000 % STATUS_STEPS.size());
        return STATUS_STEPS.get(index);
    }
}