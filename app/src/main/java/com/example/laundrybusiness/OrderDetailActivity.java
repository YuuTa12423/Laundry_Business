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

// Import the external adapter and data model
import com.example.laundrybusiness.TimelineAdapter.TimelineStep;

public class OrderDetailActivity extends AppCompatActivity {

    private String orderId;
    private TextView orderDetailHeader;
    private RecyclerView recyclerTimeline;
    private Handler handler = new Handler();
    private Runnable trackingRunnable;
    private TimelineAdapter timelineAdapter;

    // Define the fixed steps for the timeline (This should be fetched from API in production)
    private final List<TimelineStep> fixedTimelineSteps = createFixedTimelineSteps();

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

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the polling loop when the user returns to the screen
        if (trackingRunnable != null) {
            handler.post(trackingRunnable);
        }
    }

    // --- Implementation for startTrackingUpdates() (Fills TODO) ---
    private void startTrackingUpdates() {
        trackingRunnable = new Runnable() {
            @Override
            public void run() {
                // 1. API Call Simulation: FETCH LIVE STATUS
                // String currentStatus = API.fetchStatus(orderId); // Replace with real API call
                String currentStatus = getSimulatedStatus();

                // 2. Update the UI based on the new status
                updateTimelineUI(currentStatus);

                // 3. Schedule the next check (Polling)
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
            // Scroll to the current item (optional but recommended)
            // Note: In a real app, you would calculate the item's position based on the status.
        }
    }

    // --- DEFINES THE FIXED TRACKING STEPS (Order matters!) ---
    private List<TimelineStep> createFixedTimelineSteps() {
        List<TimelineStep> steps = new ArrayList<>();
        // Status codes must match what your Admin Panel sends!
        steps.add(new TimelineStep("Order Placed", "PLACED", "Just now"));
        steps.add(new TimelineStep("Picked Up by Driver", "COLLECTED", "Awaiting update..."));
        steps.add(new TimelineStep("Washing & Processing", "IN_WASH", "Awaiting update..."));
        steps.add(new TimelineStep("Ready for Pickup", "READY_FOR_PICKUP", "Awaiting update..."));
        steps.add(new TimelineStep("Delivered/Closed", "DELIVERED", "Awaiting update..."));
        return steps;
    }

    // --- TEMPORARY SIMULATION METHOD (REMOVE IN PRODUCTION) ---
    // Simulates the status progression by cycling through the fixed steps every few seconds.
    private String getSimulatedStatus() {
        // Defines the time each status should last before moving to the next
        long stepDuration = 5000; // 5 seconds per step
        long totalCycleTime = fixedTimelineSteps.size() * stepDuration;

        long elapsedTime = System.currentTimeMillis() % totalCycleTime;
        int index = (int) (elapsedTime / stepDuration);

        return fixedTimelineSteps.get(index).dataStatus;
    }
}