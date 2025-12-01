// File: OrderDetailActivity.java

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private String orderId;
    private TextView orderDetailHeader;
    private RecyclerView recyclerTimeline;
    private Handler handler = new Handler();
    private Runnable trackingRunnable;
    private TimelineAdapter timelineAdapter;

    // Define the fixed steps for the timeline
    private final List<TimelineAdapter.TimelineStep> fixedTimelineSteps = createFixedTimelineSteps();

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

        // 1. Get the Order ID passed from the previous activity
        if (getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getStringExtra("ORDER_ID");
            orderDetailHeader.setText(String.format(Locale.getDefault(), "Tracking Order %s", orderId));
        } else {
            Toast.makeText(this, "Error: No Order ID provided.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Setup the Timeline RecyclerView
        recyclerTimeline.setLayoutManager(new LinearLayoutManager(this));

        // 3. Start the tracking update process
        startTrackingUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the polling loop when the user leaves the screen
        if (trackingRunnable != null) {
            handler.removeCallbacks(trackingRunnable);
        }
    }

    // --- Core Polling Logic (Replaces the old TODO) ---
    private void startTrackingUpdates() {
        trackingRunnable = new Runnable() {
            @Override
            public void run() {
                // 1. API Call Simulation: This is where you call your backend!
                String currentStatus = getSimulatedStatus(); // Replace with API.fetchStatus(orderId)

                // 2. Update the UI based on the new status
                updateTimelineUI(currentStatus);

                // 3. Continue polling only if the order is not fully delivered
                if (!currentStatus.equals("DELIVERED")) {
                    // Poll the API every 10 seconds (10000ms)
                    handler.postDelayed(this, 10000);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order " + orderId + " is COMPLETE.", Toast.LENGTH_LONG).show();
                }
            }
        };
        // Start the initial check immediately
        handler.post(trackingRunnable);
    }

    // --- Utility Methods ---
    private void updateTimelineUI(String currentStatus) {
        if (timelineAdapter == null) {
            // Initialize the adapter on the first update
            timelineAdapter = new TimelineAdapter(fixedTimelineSteps, currentStatus);
            recyclerTimeline.setAdapter(timelineAdapter);
        } else {
            // Update the existing adapter with the new status
            timelineAdapter.currentStatus = currentStatus;
            timelineAdapter.notifyDataSetChanged();
        }
    }

    // --- DEFINES THE FIXED TRACKING STEPS (Order matters!) ---
    private List<TimelineAdapter.TimelineStep> createFixedTimelineSteps() {
        List<TimelineAdapter.TimelineStep> steps = new ArrayList<>();
        // Title, Status Code (must match server), Placeholder Timestamp
        steps.add(new TimelineAdapter.TimelineStep("Order Placed", "PLACED", "Just now"));
        steps.add(new TimelineAdapter.TimelineStep("Ready for Pickup", "READY_FOR_PICKUP", "30 mins ago"));
        steps.add(new TimelineAdapter.TimelineStep("In Transit", "IN_TRANSIT", "15 mins ago"));
        steps.add(new TimelineAdapter.TimelineStep("Delivered", "DELIVERED", "1 min ago"));
        return steps;
    }

    // --- TEMPORARY SIMULATION METHOD (REMOVE IN PRODUCTION) ---
    private String getSimulatedStatus() {
        // Simulates the status progression: PLACED -> READY_FOR_PICKUP -> IN_TRANSIT -> DELIVERED
        long totalTime = 40000; // 40 seconds cycle
        long elapsedTime = System.currentTimeMillis() % totalTime;

        if (elapsedTime < 10000) return "PLACED";
        if (elapsedTime < 20000) return "READY_FOR_PICKUP";
        if (elapsedTime < 30000) return "IN_TRANSIT";
        return "DELIVERED";
    }
}